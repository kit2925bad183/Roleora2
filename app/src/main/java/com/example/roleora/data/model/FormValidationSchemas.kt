package com.example.roleora.data.model

/**
 * Single field validation error.
 */
data class FieldValidationError(
    val fieldName: String,
    val errorMessage: String
)

/**
 * Aggregate Form Validation Result.
 */
data class FormValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap()
) {
    fun getError(fieldName: String): String? = errors[fieldName]

    companion object {
        val VALID = FormValidationResult(isValid = true, errors = emptyMap())

        fun invalid(errors: Map<String, String>): FormValidationResult {
            return FormValidationResult(isValid = errors.isEmpty(), errors = errors)
        }
    }
}

/**
 * Generic Validation Schema Interface.
 */
interface FormValidationSchema<T : ProfessionFormPayload> {
    fun validate(form: T): FormValidationResult
}

/**
 * Screenplay Metadata Validation Schema.
 */
object ScreenplayMetaValidationSchema : FormValidationSchema<ScreenplayMetaForm> {
    override fun validate(form: ScreenplayMetaForm): FormValidationResult {
        val errors = mutableMapOf<String, String>()

        if (form.title.trim().isEmpty()) {
            errors["title"] = "Screenplay title is required."
        } else if (form.title.trim().length > 120) {
            errors["title"] = "Title cannot exceed 120 characters."
        }

        if (form.logline.trim().isEmpty()) {
            errors["logline"] = "Logline is required for screenplay records."
        } else if (form.logline.trim().length < 10) {
            errors["logline"] = "Logline must be at least 10 characters long."
        }

        if (form.pageCount <= 0) {
            errors["pageCount"] = "Page count must be at least 1 page."
        } else if (form.pageCount > 500) {
            errors["pageCount"] = "Page count exceeds standard feature limit (500 pages)."
        }

        if (form.sceneCount <= 0) {
            errors["sceneCount"] = "Scene count must be at least 1 scene."
        }

        if (form.targetRuntimeMinutes <= 0) {
            errors["targetRuntimeMinutes"] = "Runtime must be greater than 0 minutes."
        }

        if (form.estimatedBudgetUsd < 0) {
            errors["estimatedBudgetUsd"] = "Budget cannot be negative."
        }

        return FormValidationResult.invalid(errors)
    }
}

/**
 * Student Attendance & Academic Subject Validation Schema.
 */
object StudentAttendanceValidationSchema : FormValidationSchema<StudentAttendanceForm> {
    override fun validate(form: StudentAttendanceForm): FormValidationResult {
        val errors = mutableMapOf<String, String>()

        if (form.subjectCode.trim().isEmpty()) {
            errors["subjectCode"] = "Subject code is required (e.g., CS-301)."
        }

        if (form.subjectName.trim().isEmpty()) {
            errors["subjectName"] = "Subject / Course name is required."
        }

        if (form.totalClasses < 0) {
            errors["totalClasses"] = "Total classes cannot be negative."
        }

        if (form.attendedClasses < 0) {
            errors["attendedClasses"] = "Attended classes cannot be negative."
        }

        if (form.attendedClasses > form.totalClasses && form.totalClasses > 0) {
            errors["attendedClasses"] = "Attended classes cannot exceed total classes conducted."
        }

        if (form.minimumRequiredAttendancePercent !in 0.0..100.0) {
            errors["minimumRequiredAttendancePercent"] = "Threshold must be between 0% and 100%."
        }

        if (form.semester !in 1..12) {
            errors["semester"] = "Semester must be between 1 and 12."
        }

        if (form.creditHours <= 0) {
            errors["creditHours"] = "Credit hours must be at least 1."
        }

        return FormValidationResult.invalid(errors)
    }
}

/**
 * Farm Inventory & Crop Management Validation Schema.
 */
object FarmInventoryValidationSchema : FormValidationSchema<FarmInventoryForm> {
    override fun validate(form: FarmInventoryForm): FormValidationResult {
        val errors = mutableMapOf<String, String>()

        if (form.itemName.trim().isEmpty()) {
            errors["itemName"] = "Item or Crop variety name is required."
        }

        if (form.fieldOrParcelId.trim().isEmpty()) {
            errors["fieldOrParcelId"] = "Field/Parcel ID or Storage Bin identifier is required."
        }

        if (form.quantityOnHand < 0) {
            errors["quantityOnHand"] = "Quantity on hand cannot be negative."
        }

        if (form.reorderThreshold < 0) {
            errors["reorderThreshold"] = "Reorder threshold cannot be negative."
        }

        if (form.costPerUnitUsd < 0) {
            errors["costPerUnitUsd"] = "Unit cost cannot be negative."
        }

        if (form.soilConditionOrPh !in 0.0..14.0) {
            errors["soilConditionOrPh"] = "Soil pH must be between 0.0 and 14.0."
        }

        return FormValidationResult.invalid(errors)
    }
}

/**
 * Software Developer Project & PR Validation Schema.
 */
object DevProjectValidationSchema : FormValidationSchema<DevProjectForm> {
    override fun validate(form: DevProjectForm): FormValidationResult {
        val errors = mutableMapOf<String, String>()

        if (form.projectName.trim().isEmpty()) {
            errors["projectName"] = "Project or Module name is required."
        }

        if (form.branchName.trim().isEmpty()) {
            errors["branchName"] = "Target git branch name is required."
        }

        if (form.storyPoints < 0) {
            errors["storyPoints"] = "Story points cannot be negative."
        }

        if (form.pullRequestNumber < 0) {
            errors["pullRequestNumber"] = "PR number cannot be negative."
        }

        return FormValidationResult.invalid(errors)
    }
}

/**
 * Photography Shoot & Client Booking Validation Schema.
 */
object PhotoShootValidationSchema : FormValidationSchema<PhotoShootForm> {
    override fun validate(form: PhotoShootForm): FormValidationResult {
        val errors = mutableMapOf<String, String>()

        if (form.clientName.trim().isEmpty()) {
            errors["clientName"] = "Client name or organization is required."
        }

        if (form.location.trim().isEmpty()) {
            errors["location"] = "Shoot location or venue address is required."
        }

        if (form.totalFeeUsd < 0) {
            errors["totalFeeUsd"] = "Shoot fee cannot be negative."
        }

        if (form.shotCountEstimate <= 0) {
            errors["shotCountEstimate"] = "Estimated shot count must be greater than 0."
        }

        return FormValidationResult.invalid(errors)
    }
}
