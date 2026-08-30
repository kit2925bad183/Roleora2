package com.example.roleora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Core Role Entity representing an isolated user workspace.
 */
@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey val id: String,
    val userId: String = "user_default",
    val templateId: String,
    val templateVersion: String = "1.0.0",
    val displayName: String,
    val category: String,
    val specialisation: String,
    val roleTitle: String = "",
    val institutionOrOrg: String = "",
    val experienceLevel: String = "Intermediate",
    val workType: String = "Independent", // Independent, Team, Organisation
    val workFormat: String = "Diary & Projects",
    val teamSize: String = "1-5",
    val location: String = "Local",
    val language: String = "English",
    val timezone: String = "UTC+05:30",
    val iconName: String = "work",
    val colorHex: String = "#8B5CF6",
    val aiEnabled: Boolean = false,
    val aiPermissions: String = "[]", // JSON array of granted permission scopes
    val isPrivate: Boolean = true,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Profession Template entity defining predefined role setups.
 */
@Entity(tableName = "profession_templates")
data class ProfessionTemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val iconName: String,
    val defaultColor: String,
    val defaultSpecialisations: String, // Comma-separated or JSON list
    val defaultModules: String, // JSON list of module IDs
    val defaultWorkflow: String, // Comma-separated workflow stages
    val currentVersion: String = "1.0.0",
    val isOfficial: Boolean = true,
    val popularityRank: Int = 1
)

/**
 * Version record for each template to support migrations and rollbacks.
 */
@Entity(tableName = "template_versions")
data class TemplateVersionEntity(
    @PrimaryKey val id: String, // e.g. "movie_director_1.0.0"
    val templateId: String,
    val versionNumber: String,
    val status: String = "Published", // Draft, Testing, Published, Deprecated
    val releaseDate: String,
    val changeSummary: String,
    val addedModules: String = "[]",
    val removedModules: String = "[]",
    val schemaChanges: String = "None",
    val workflowChanges: String = "None",
    val minAppVersion: String = "1.0.0",
    val isCompatible: Boolean = true,
    val migrationDefinition: String = ""
)

/**
 * Installation tracking for roles to discover updates and rollback.
 */
@Entity(tableName = "template_installations")
data class TemplateInstallationEntity(
    @PrimaryKey val id: String, // roleId
    val roleId: String,
    val templateId: String,
    val installedVersion: String,
    val installedAt: Long = System.currentTimeMillis(),
    val isCustomized: Boolean = false,
    val lastBackupConfigJson: String = ""
)

/**
 * Universal Diary Entry entity scoped to a role.
 */
@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey val id: String,
    val roleId: String,
    val title: String,
    val content: String,
    val entryType: String = "General Note",
    val activityDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: String = "",
    val securityLevel: String = "Private",
    val isPinned: Boolean = false,
    val moodOrStatus: String = "Normal"
)

/**
 * Profession Specific Records for deep specialized workflows.
 */
@Entity(tableName = "profession_records")
data class ProfessionRecordEntity(
    @PrimaryKey val id: String,
    val roleId: String,
    val professionType: String, // DIRECTOR, STUDENT, DEVELOPER, PHOTOGRAPHER, FARMER
    val recordCategory: String, // e.g. "SCREENPLAY", "SHOT", "ATTENDANCE", "SUBJECT", "KANBAN", "SNIPPET", "BOOKING", "CROP", "IRRIGATION"
    val title: String,
    val subtitle: String = "",
    val stage: String = "",
    val status: String = "Active",
    val dateOrDeadline: Long = System.currentTimeMillis(),
    val numericValue1: Double = 0.0, // e.g., CGPA / Attendance % / Budget / Acres / Quantity
    val numericValue2: Double = 0.0, // e.g., Max marks / Total classes / Price / Shots pending
    val detailsJson: String = "{}",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Audit log for role actions, version updates, and security checkpoints.
 */
@Entity(tableName = "audit_events")
data class AuditEventEntity(
    @PrimaryKey val id: String,
    val roleId: String,
    val eventType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadataJson: String = "{}"
)

/**
 * Versioned Template Configuration Snapshot for individual workspaces.
 * Allows users to save point-in-time configuration baselines and revert seamlessly.
 */
@Entity(tableName = "workspace_template_versions")
data class WorkspaceTemplateVersionEntity(
    @PrimaryKey val versionId: String,
    val roleId: String,
    val templateId: String,
    val versionNumber: String, // e.g., "1.0.0", "1.1.0-custom", "2.0.0"
    val versionLabel: String, // e.g., "Feature Film Production Baseline", "Final Exam Layout"
    val changeSummary: String, // e.g., "Added Shot List & Budget modules, altered workflow stages"
    val authorId: String = "local_owner",
    val createdAt: Long = System.currentTimeMillis(),
    val isCurrentActive: Boolean = false,
    val isLocked: Boolean = false, // Locked snapshots cannot be deleted
    val isFavorite: Boolean = false,
    val tags: String = "", // Comma-separated tags
    // Serialized snapshot of workspace configuration
    val roleDisplayName: String,
    val roleCategory: String,
    val specialisation: String,
    val roleTitle: String = "",
    val institutionOrOrg: String = "",
    val experienceLevel: String = "Intermediate",
    val workType: String = "Independent",
    val workFormat: String = "Diary & Projects",
    val teamSize: String = "1-5",
    val location: String = "Local",
    val language: String = "English",
    val timezone: String = "UTC+05:30",
    val iconName: String = "work",
    val colorHex: String = "#8B5CF6",
    val aiEnabled: Boolean = false,
    val aiPermissions: String = "[]",
    val enabledModulesJson: String = "[]", // Active module identifiers
    val workflowStages: String = "", // Comma-separated list of workflow stages
    val customFieldSchemasJson: String = "{}", // Custom form field definitions
    val customRecordCategoriesJson: String = "[]", // Custom categories list
    val rawConfigPayloadJson: String = "{}" // Extended JSON payload for full fidelity backup
)

/**
 * Result model when comparing two workspace template configurations.
 */
data class TemplateDiffResult(
    val baseVersionNumber: String,
    val targetVersionNumber: String,
    val addedModules: List<String> = emptyList(),
    val removedModules: List<String> = emptyList(),
    val retainedModules: List<String> = emptyList(),
    val workflowChanges: List<String> = emptyList(),
    val specialisationChanged: Boolean = false,
    val oldSpecialisation: String = "",
    val newSpecialisation: String = "",
    val colorChanged: Boolean = false,
    val oldColorHex: String = "",
    val newColorHex: String = "",
    val iconChanged: Boolean = false,
    val oldIcon: String = "",
    val newIcon: String = "",
    val aiStateChanged: Boolean = false,
    val oldAiEnabled: Boolean = false,
    val newAiEnabled: Boolean = false,
    val roleTitleChanged: Boolean = false,
    val oldRoleTitle: String = "",
    val newRoleTitle: String = "",
    val summaryText: String = ""
)

