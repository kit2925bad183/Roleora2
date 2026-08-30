package com.example.roleora.data.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Encapsulates the runtime state of an interactive profession form with draft support.
 */
data class FormState<T : ProfessionFormPayload>(
    val initialData: T,
    val currentData: T,
    val isDirty: Boolean = false,
    val isSavingDraft: Boolean = false,
    val lastDraftSavedAt: Long? = null,
    val validationResult: FormValidationResult = FormValidationResult.VALID,
    val isSubmitting: Boolean = false,
    val submitError: String? = null
)

/**
 * Base Form State Manager handling draft persistence, validation triggers,
 * dirty state checking, and auto-save debouncing.
 */
class DraftFormStateManager<T : ProfessionFormPayload>(
    val roleId: String,
    val draftKey: String,
    val initialData: T,
    private val validator: FormValidationSchema<T>,
    private val scope: CoroutineScope,
    private val onPersistDraft: (suspend (roleId: String, draftKey: String, data: T) -> Unit)? = null,
    private val autoSaveDebounceMs: Long = 1500L
) {
    private val _formState = MutableStateFlow(
        FormState(
            initialData = initialData,
            currentData = initialData,
            isDirty = false,
            validationResult = validator.validate(initialData)
        )
    )
    val formState: StateFlow<FormState<T>> = _formState.asStateFlow()

    private var autoSaveJob: Job? = null

    /**
     * Updates field value and triggers debounced auto-save & validation.
     */
    fun update(transform: (T) -> T) {
        val updated = transform(_formState.value.currentData)
        val isNowDirty = updated != _formState.value.initialData
        val validation = validator.validate(updated)

        _formState.value = _formState.value.copy(
            currentData = updated,
            isDirty = isNowDirty,
            validationResult = validation,
            submitError = null
        )

        scheduleAutoSave(updated)
    }

    /**
     * Replaces form data with a restored draft.
     */
    fun restoreDraft(draft: T) {
        _formState.value = _formState.value.copy(
            currentData = draft,
            isDirty = draft != _formState.value.initialData,
            validationResult = validator.validate(draft)
        )
    }

    /**
     * Resets form to initial baseline, discarding any unsaved edits.
     */
    fun resetToInitial() {
        autoSaveJob?.cancel()
        _formState.value = FormState(
            initialData = initialData,
            currentData = initialData,
            isDirty = false,
            validationResult = validator.validate(initialData)
        )
    }

    /**
     * Validates current form data synchronously.
     */
    fun validate(): Boolean {
        val result = validator.validate(_formState.value.currentData)
        _formState.value = _formState.value.copy(validationResult = result)
        return result.isValid
    }

    /**
     * Commits the draft immediately.
     */
    fun saveDraftNow() {
        autoSaveJob?.cancel()
        val dataToSave = _formState.value.currentData
        scope.launch(Dispatchers.IO) {
            _formState.value = _formState.value.copy(isSavingDraft = true)
            onPersistDraft?.invoke(roleId, draftKey, dataToSave)
            _formState.value = _formState.value.copy(
                isSavingDraft = false,
                lastDraftSavedAt = System.currentTimeMillis()
            )
        }
    }

    private fun scheduleAutoSave(dataToSave: T) {
        if (onPersistDraft == null) return
        autoSaveJob?.cancel()
        autoSaveJob = scope.launch(Dispatchers.IO) {
            delay(autoSaveDebounceMs)
            _formState.value = _formState.value.copy(isSavingDraft = true)
            onPersistDraft.invoke(roleId, draftKey, dataToSave)
            _formState.value = _formState.value.copy(
                isSavingDraft = false,
                lastDraftSavedAt = System.currentTimeMillis()
            )
        }
    }

    /**
     * Clears running debounce jobs on lifecycle teardown.
     */
    fun dispose() {
        autoSaveJob?.cancel()
    }
}

/**
 * In-memory registry for active drafts across role workspaces.
 */
object DraftStoreRegistry {
    private val activeDrafts = mutableMapOf<String, Any>()

    fun <T : Any> getDraft(roleId: String, formKey: String): T? {
        val compositeKey = "${roleId}_$formKey"
        @Suppress("UNCHECKED_CAST")
        return activeDrafts[compositeKey] as? T
    }

    fun <T : Any> saveDraft(roleId: String, formKey: String, draft: T) {
        val compositeKey = "${roleId}_$formKey"
        activeDrafts[compositeKey] = draft
    }

    fun clearDraft(roleId: String, formKey: String) {
        val compositeKey = "${roleId}_$formKey"
        activeDrafts.remove(compositeKey)
    }

    fun clearAllDraftsForRole(roleId: String) {
        val prefix = "${roleId}_"
        activeDrafts.keys.removeAll { it.startsWith(prefix) }
    }
}
