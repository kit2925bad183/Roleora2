/**
 * ROLEORA - Cross-Platform Shared Profession Forms & Validation Types
 * TypeScript definitions mirroring Kotlin data models and Firestore schemas.
 */

export type ProfessionFormType =
  | 'SCREENPLAY_META'
  | 'STUDENT_ATTENDANCE'
  | 'FARM_INVENTORY'
  | 'DEV_PROJECT'
  | 'PHOTO_SHOOT';

/**
 * Base Profession Form Interface
 */
export interface BaseProfessionFormPayload {
  formType: ProfessionFormType;
  formVersion: string;
  lastEditedTimestamp: number;
}

// -----------------------------------------------------------------------------
// 1. Movie Director / Filmmaker: Screenplay & Production Meta
// -----------------------------------------------------------------------------
export interface ScreenplayMetaForm extends BaseProfessionFormPayload {
  formType: 'SCREENPLAY_META';
  title: string;
  logline: string;
  genre: string;
  draftVersion: string;
  pageCount: number;
  sceneCount: number;
  targetRuntimeMinutes: number;
  shootingLocations: string[];
  primaryProtagonist: string;
  antagonist: string;
  loglinePitch: string;
  estimatedBudgetUsd: number;
  productionStage: 'Scriptwriting' | 'Pre-Production' | 'Shooting' | 'Post-Production' | 'Distribution';
  pacingNotes: string;
  copyrightRegistrationNumber: string;
  isWgaRegistered: boolean;
}

// -----------------------------------------------------------------------------
// 2. College Student / Academic: Attendance & Subject Meta
// -----------------------------------------------------------------------------
export interface StudentAttendanceForm extends BaseProfessionFormPayload {
  formType: 'STUDENT_ATTENDANCE';
  subjectCode: string;
  subjectName: string;
  professorName: string;
  attendedClasses: number;
  totalClasses: number;
  minimumRequiredAttendancePercent: number;
  semester: number;
  creditHours: number;
  classroomOrHall: string;
  gradingScale: string;
  currentGradeOrScore: number;
  nextExamDate?: number | null;
  pendingAssignmentsCount: number;
  academicNotes: string;
}

// -----------------------------------------------------------------------------
// 3. Farmer / Agricultural Manager: Crop & Inventory Meta
// -----------------------------------------------------------------------------
export interface FarmInventoryForm extends BaseProfessionFormPayload {
  formType: 'FARM_INVENTORY';
  itemName: string;
  itemCategory: 'SEED' | 'FERTILIZER' | 'PESTICIDE' | 'CROP_HARVEST' | 'EQUIPMENT_PARTS' | 'LIVESTOCK_FEED';
  fieldOrParcelId: string;
  quantityOnHand: number;
  measurementUnit: string; // 'kg' | 'tonnes' | 'liters' | 'bags' | 'bushels' | 'acres'
  reorderThreshold: number;
  costPerUnitUsd: number;
  supplierOrSource: string;
  storageLocation: string;
  expiryOrHarvestDate?: number | null;
  batchOrLotNumber: string;
  soilConditionOrPh: number;
  irrigationFrequency: 'Daily' | 'Alternate Days' | 'Weekly' | 'Bi-Weekly';
  organicCertified: boolean;
  storageTemperatureCelsius: number;
  notesAndAlerts: string;
}

// -----------------------------------------------------------------------------
// 4. Software Developer: Project & Sprint Task Meta
// -----------------------------------------------------------------------------
export interface DevProjectForm extends BaseProfessionFormPayload {
  formType: 'DEV_PROJECT';
  projectName: string;
  repositoryUrl: string;
  branchName: string;
  pullRequestTitle: string;
  pullRequestNumber: number;
  storyPoints: number;
  priority: 'Low' | 'Medium' | 'High' | 'Critical';
  techStack: string[];
  ciStatus: 'Passing' | 'Failing' | 'In-Progress' | 'Skipped';
  targetReleaseSprint: string;
  architecturalNotes: string;
}

// -----------------------------------------------------------------------------
// 5. Photographer: Shoot & Client Booking Meta
// -----------------------------------------------------------------------------
export interface PhotoShootForm extends BaseProfessionFormPayload {
  formType: 'PHOTO_SHOOT';
  clientName: string;
  shootType: 'Portrait' | 'Wedding' | 'Commercial' | 'Wildlife' | 'Landscape';
  shootDate: number;
  location: string;
  packageTier: string;
  primaryCameraBody: string;
  primaryLens: string;
  shotCountEstimate: number;
  agreedDeliveryDeadline: number;
  totalFeeUsd: number;
  depositReceived: boolean;
  rawStorageDriveId: string;
  clientSpecialRequests: string;
}

/**
 * Firestore Document Model Schema for Storing Profession Records & Drafts
 */
export interface FirestoreProfessionRecordDocument<T extends BaseProfessionFormPayload = BaseProfessionFormPayload> {
  id: string;
  roleId: string;
  formType: ProfessionFormType;
  formVersion: string;
  title: string;
  subtitle: string;
  details: Omit<T, 'formType' | 'formVersion' | 'lastEditedTimestamp'>;
  isDraft: boolean;
  draftSessionId?: string | null;
  createdAt: number;
  updatedAt: number;
}

/**
 * Validation Result Definition
 */
export interface FormValidationResult {
  isValid: boolean;
  errors: Record<string, string>;
}
