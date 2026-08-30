package com.example.roleora.data.local

import com.example.roleora.data.model.ProfessionTemplateEntity
import com.example.roleora.data.model.TemplateVersionEntity

object SeedData {
    val initialTemplates = listOf(
        ProfessionTemplateEntity(
            id = "movie_director",
            name = "Movie Director",
            category = "Creative & Media",
            description = "Cinematic production system with screenplay editor, script breakdown, shot lists, call sheets, casting, and daily shooting diary.",
            iconName = "movie",
            defaultColor = "#F59E0B",
            defaultSpecialisations = "Short film, Feature film, Documentary, Advertisement, Music video, Web series, YouTube content, Animation, Indie Cinema",
            defaultModules = """["dashboard", "productions", "screenplay", "breakdown", "shots", "diary", "budget", "continuity", "cast_crew"]""",
            defaultWorkflow = "Idea, Story, Screenplay, Pre-production, Rehearsal, Production, Post-production, Review, Release, Archive",
            currentVersion = "1.1.0",
            popularityRank = 1
        ),
        ProfessionTemplateEntity(
            id = "college_student",
            name = "College Student",
            category = "Academic & Education",
            description = "Curriculum workspace with timetable, assignments, attendance warnings, marks/CGPA calculation, study goals, and exam planner.",
            iconName = "school",
            defaultColor = "#6366F1",
            defaultSpecialisations = "Engineering, Arts & Science, Medicine, Law, Business, Agriculture, Design, Media, B.Tech AI & Data Science",
            defaultModules = """["dashboard", "timetable", "attendance", "assignments", "exams", "cgpa", "study_planner", "diary"]""",
            defaultWorkflow = "Semester, Subject, Learning, Assignment, Examination, Result, Progress Review",
            currentVersion = "1.1.0",
            popularityRank = 2
        ),
        ProfessionTemplateEntity(
            id = "software_developer",
            name = "Software Developer",
            category = "Technology & Engineering",
            description = "Engineering workspace with sprint Kanban, code snippets repository, API & architecture notes, bug tracking, and developer diary.",
            iconName = "code",
            defaultColor = "#06B6D4",
            defaultSpecialisations = "Frontend, Backend, Full stack, Mobile, Desktop, Game development, Cloud/DevOps, Embedded Systems, AI Engineering",
            defaultModules = """["dashboard", "kanban", "snippets", "bugs", "api_notes", "deployments", "diary", "work_timer"]""",
            defaultWorkflow = "Requirement, Design, Development, Code Review, Testing, Deployment, Maintenance",
            currentVersion = "1.1.0",
            popularityRank = 3
        ),
        ProfessionTemplateEntity(
            id = "photographer",
            name = "Photographer",
            category = "Creative & Media",
            description = "Photography studio management with client bookings, shoot planner, shot lists & moodboards, equipment health, and delivery queue.",
            iconName = "camera",
            defaultColor = "#EC4899",
            defaultSpecialisations = "Wedding, Event, Wildlife, Product, Fashion, Portrait, Travel, Sports, Street & Documentary",
            defaultModules = """["dashboard", "bookings", "shoots", "shot_lists", "equipment", "invoices", "media_vault", "diary"]""",
            defaultWorkflow = "Enquiry, Consultation, Booking, Planning, Shooting, Editing, Client Review, Delivery, Payment, Archive",
            currentVersion = "1.1.0",
            popularityRank = 4
        ),
        ProfessionTemplateEntity(
            id = "farmer",
            name = "Farmer",
            category = "Agriculture & Environment",
            description = "Field operations hub with crop life cycle, irrigation schedules, treatment reminders, equipment maintenance, and harvest sales ledger.",
            iconName = "eco",
            defaultColor = "#10B981",
            defaultSpecialisations = "Field crops, Horticulture, Organic farming, Dairy, Poultry, Fisheries, Mixed farming, Agroforestry",
            defaultModules = """["dashboard", "crops", "irrigation", "treatments", "equipment", "harvest", "sales", "diary"]""",
            defaultWorkflow = "Planning, Land Preparation, Planting, Growth, Treatment, Harvest, Storage, Sale",
            currentVersion = "1.1.0",
            popularityRank = 5
        )
    )

    val templateVersions = listOf(
        // Movie Director Versions
        TemplateVersionEntity(
            id = "movie_director_1.0.0",
            templateId = "movie_director",
            versionNumber = "1.0.0",
            status = "Published",
            releaseDate = "2026-06-01",
            changeSummary = "Initial release of Movie Director pack with screenplay editor and shot list tracker.",
            addedModules = """["dashboard", "productions", "screenplay", "breakdown", "shots", "diary"]""",
            schemaChanges = "Base schema for scenes, breakdown elements and take tracking.",
            workflowChanges = "Standard 9-stage film production workflow.",
            minAppVersion = "1.0.0",
            isCompatible = true
        ),
        TemplateVersionEntity(
            id = "movie_director_1.1.0",
            templateId = "movie_director",
            versionNumber = "1.1.0",
            status = "Published",
            releaseDate = "2026-08-15",
            changeSummary = "Added Budget Tracking module, enhanced Call Sheet generator, and scene continuity logs.",
            addedModules = """["budget", "continuity", "cast_crew"]""",
            removedModules = "[]",
            schemaChanges = "Added expense ledger and continuity takes metadata.",
            workflowChanges = "Added Rehearsal & Release milestones.",
            minAppVersion = "1.0.0",
            isCompatible = true,
            migrationDefinition = "Auto-migrates existing screenplay scenes while enabling budget and continuity."
        ),

        // College Student Versions
        TemplateVersionEntity(
            id = "college_student_1.0.0",
            templateId = "college_student",
            versionNumber = "1.0.0",
            status = "Published",
            releaseDate = "2026-06-01",
            changeSummary = "Initial release with timetable, subject tracking, and attendance counter.",
            addedModules = """["dashboard", "timetable", "attendance", "assignments", "exams", "diary"]""",
            schemaChanges = "Base schema for academic courses and attendance logs.",
            minAppVersion = "1.0.0",
            isCompatible = true
        ),
        TemplateVersionEntity(
            id = "college_student_1.1.0",
            templateId = "college_student",
            versionNumber = "1.1.0",
            status = "Published",
            releaseDate = "2026-08-15",
            changeSummary = "Added Weighted CGPA Calculator, Study Planner Goals, and low-attendance alert triggers.",
            addedModules = """["cgpa", "study_planner"]""",
            schemaChanges = "Added grading scale customization and study session timer logs.",
            minAppVersion = "1.0.0",
            isCompatible = true,
            migrationDefinition = "Preserves all existing subject attendances and adds credit weight options."
        ),

        // Software Developer Versions
        TemplateVersionEntity(
            id = "software_developer_1.0.0",
            templateId = "software_developer",
            versionNumber = "1.0.0",
            status = "Published",
            releaseDate = "2026-06-01",
            changeSummary = "Initial release with sprint Kanban board and bug tracker.",
            addedModules = """["dashboard", "kanban", "bugs", "snippets", "diary"]""",
            minAppVersion = "1.0.0",
            isCompatible = true
        ),
        TemplateVersionEntity(
            id = "software_developer_1.1.0",
            templateId = "software_developer",
            versionNumber = "1.1.0",
            status = "Published",
            releaseDate = "2026-08-15",
            changeSummary = "Added API Endpoint Notes, Deployment environment tracker, and developer work timer.",
            addedModules = """["api_notes", "deployments", "work_timer"]""",
            schemaChanges = "Added HTTP method/payload schemas and server environment states.",
            minAppVersion = "1.0.0",
            isCompatible = true,
            migrationDefinition = "Migrates existing Kanban tasks and links API schemas seamlessly."
        ),

        // Photographer Versions
        TemplateVersionEntity(
            id = "photographer_1.0.0",
            templateId = "photographer",
            versionNumber = "1.0.0",
            status = "Published",
            releaseDate = "2026-06-01",
            changeSummary = "Initial release with client booking manager and shoot planner.",
            addedModules = """["dashboard", "bookings", "shoots", "equipment", "diary"]""",
            minAppVersion = "1.0.0",
            isCompatible = true
        ),
        TemplateVersionEntity(
            id = "photographer_1.1.0",
            templateId = "photographer",
            versionNumber = "1.1.0",
            status = "Published",
            releaseDate = "2026-08-15",
            changeSummary = "Added Invoicing & Advance tracking, Shot List moodboard, and Media Vault proofing.",
            addedModules = """["invoices", "shot_lists", "media_vault"]""",
            schemaChanges = "Added payment breakdown and client delivery review fields.",
            minAppVersion = "1.0.0",
            isCompatible = true,
            migrationDefinition = "Safely upgrades shoot details with payment and delivery tracking."
        ),

        // Farmer Versions
        TemplateVersionEntity(
            id = "farmer_1.0.0",
            templateId = "farmer",
            versionNumber = "1.0.0",
            status = "Published",
            releaseDate = "2026-06-01",
            changeSummary = "Initial release with crop lifecycle, field parcel logs, and irrigation schedules.",
            addedModules = """["dashboard", "crops", "irrigation", "equipment", "diary"]""",
            minAppVersion = "1.0.0",
            isCompatible = true
        ),
        TemplateVersionEntity(
            id = "farmer_1.1.0",
            templateId = "farmer",
            versionNumber = "1.1.0",
            status = "Published",
            releaseDate = "2026-08-15",
            changeSummary = "Added Treatment / Pest Log with safety notes, Harvest Yield Forecast, and Sales Ledger.",
            addedModules = """["treatments", "harvest", "sales"]""",
            schemaChanges = "Added dosage safety observations and buyer market price logs.",
            minAppVersion = "1.0.0",
            isCompatible = true,
            migrationDefinition = "Extends field crops with treatment records and market transactions."
        )
    )
}
