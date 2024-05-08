export enum OffCanvasAction {
  OPEN = 'OPEN',
  CLOSE = 'CLOSE',
  TOGGLE = 'TOGGLE',
}

export enum Language {
  de = 'de',
  it = 'it',
}

export class SimpleMessageDTO {
  static fromDTO(
    dto: SimpleMessageDTO | undefined
  ): SimpleMessageDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new SimpleMessageDTO(dto.resultCode, dto.message, dto.errorMessages);
  }
  constructor(
    public resultCode: number,
    public message: string,
    public errorMessages: string[]
  ) {}
}

export class CreationTaskDTO {
  static fromDTO(
    dto: CreationTaskDTO | undefined
  ): CreationTaskDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new CreationTaskDTO(dto.id, dto.taskName, dto.desc, dto.category);
  }
  static fromDTOs(dtos: CreationTaskDTO[]): CreationTaskDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => CreationTaskDTO.fromDTO(dto))
      .filter((dto): dto is CreationTaskDTO => !!dto);
  }
  constructor(
    public id: number | undefined,
    public taskName: string,
    public desc: string,
    public category: string
  ) {}
}

export class TextDTO {
  static fromDTO(dto: TextDTO | undefined): TextDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new TextDTO(
      dto.id,
      dto.altId,
      dto.textdata,
      dto.languageCode,
      CreationTaskDTO.fromDTO(dto.creationTask),
      dto.lastModified
    );
  }

  static fromDTOs(dtos: TextDTO[]): TextDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => TextDTO.fromDTO(dto))
      .filter((dto): dto is TextDTO => !!dto);
  }

  constructor(
    public id: string | undefined,
    public altId: string,
    public textdata: string,
    public languageCode: string,
    public creationTask: CreationTaskDTO | undefined,
    public lastModified: number
  ) {}
}

export enum SpeakerGender {
  F = 'F',
  M = 'M',
  D = 'D',
}

export class TextWithMetadataDTO extends TextDTO {
  static override fromDTO(
    dto: TextWithMetadataDTO | undefined
  ): TextWithMetadataDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new TextWithMetadataDTO(
      dto.id,
      dto.altId,
      dto.textdata,
      dto.languageCode,
      CreationTaskDTO.fromDTO(dto.creationTask),
      dto.lastModified,
      dto.gender,
      dto.ageAtCreation,
      dto.l1Language,
      dto.l2Languages,
      dto.location,
      dto.tokens
    );
  }
  constructor(
    id: string | undefined,
    altId: string,
    textdata: string,
    languageCode: string,
    creationTask: CreationTaskDTO | undefined,
    lastModified: number,
    public gender: SpeakerGender | undefined,
    public ageAtCreation: number,
    public l1Language: string,
    public l2Languages: string,
    public location: string,
    public tokens: string[]
  ) {
    super(id, altId, textdata, languageCode, creationTask, lastModified);
  }
}

export class ModifierDTO {
  static fromDTO(dto: ModifierDTO | undefined): ModifierDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new ModifierDTO(dto.id, dto.modifierCode, dto.desc);
  }
  static fromDTOs(dtos: ModifierDTO[]): ModifierDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => ModifierDTO.fromDTO(dto))
      .filter((dto): dto is ModifierDTO => !!dto);
  }
  constructor(
    public id: number | undefined,
    public modifierCode: string,
    public desc: string
  ) {}
}

export class SubactDTO {
  static fromDTO(dto: SubactDTO | undefined): SubactDTO | undefined {
    if (!dto) {
      return undefined;
    }
    const parent = SubactDTO.fromDTO(dto.parentSubact);
    return new SubactDTO(
      dto.id,
      dto.subactName,
      dto.parentSubactId,
      parent,
      dto.desc
    );
  }
  static fromDTOs(dtos: SubactDTO[]): SubactDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => SubactDTO.fromDTO(dto))
      .filter((dto): dto is SubactDTO => !!dto);
  }
  constructor(
    public id: number | undefined,
    public subactName: string,
    public parentSubactId: number | undefined,
    public parentSubact: SubactDTO | undefined,
    public desc: string
  ) {}
  get fullName(): string {
    return this.parentSubact
      ? this.parentSubact.fullName + '/' + this.subactName
      : this.subactName;
  }
}

export class ModifierAnnotationDTO {
  static fromDTO(
    dto: ModifierAnnotationDTO | undefined
  ): ModifierAnnotationDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new ModifierAnnotationDTO(
      dto.id,
      dto.textId,
      dto.modifierId,
      dto.startTn,
      dto.endTn
    );
  }
  static fromDTOs(dtos: ModifierAnnotationDTO[]): ModifierAnnotationDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => ModifierAnnotationDTO.fromDTO(dto))
      .filter((dto): dto is ModifierAnnotationDTO => !!dto);
  }
  constructor(
    public id: number,
    public textId: string,
    public modifierId: number,
    public startTn: number,
    public endTn: number
  ) {}
}

export class SubactAnnotationDTO {
  static fromDTO(
    dto: SubactAnnotationDTO | undefined
  ): SubactAnnotationDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new SubactAnnotationDTO(
      dto.id,
      dto.textId,
      dto.subactId,
      dto.startTn,
      dto.endTn
    );
  }
  static fromDTOs(dtos: SubactAnnotationDTO[]): SubactAnnotationDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => SubactAnnotationDTO.fromDTO(dto))
      .filter((dto): dto is SubactAnnotationDTO => !!dto);
  }
  constructor(
    public id: number,
    public textId: string,
    public subactId: number,
    public startTn: number,
    public endTn: number
  ) {}
}

export class AnnotatingRequestDTO {
  static fromDTO(
    dto: AnnotatingRequestDTO | undefined
  ): AnnotatingRequestDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new AnnotatingRequestDTO(
      dto.text,
      dto.modifierIds,
      dto.subactIds,
      dto.language,
      dto.useMax
    );
  }
  constructor(
    public text: string,
    public modifierIds: number[],
    public subactIds: number[],
    public language: string,
    public useMax: boolean
  ) {}
}

export class TokenInfo {
  static fromDTO(dto: TokenInfo | undefined): TokenInfo | undefined {
    if (!dto) {
      return undefined;
    }
    return new TokenInfo(
      dto.original,
      dto.token,
      dto.tags,
      dto.modifierIds,
      dto.subactIds
    );
  }
  static fromDTOs(dtos: TokenInfo[]): TokenInfo[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => TokenInfo.fromDTO(dto))
      .filter((dto): dto is TokenInfo => !!dto);
  }
  constructor(
    public original: string,
    public token: string,
    public tags: string[],
    public modifierIds: number[],
    public subactIds: number[]
  ) {}
}

export class AnnotatingResult {
  static fromDTO(
    dto: AnnotatingResult | undefined
  ): AnnotatingResult | undefined {
    if (!dto) {
      return undefined;
    }
    return new AnnotatingResult(
      dto.originalText,
      TokenInfo.fromDTOs(dto.tokenInfos)
    );
  }
  constructor(public originalText: string, public tokenInfos: TokenInfo[]) {}
}

export class AnnotatingResultDTO {
  static fromDTO(
    dto: AnnotatingResultDTO | undefined
  ): AnnotatingResultDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new AnnotatingResultDTO(
      AnnotatingRequestDTO.fromDTO(dto.request),
      AnnotatingResult.fromDTO(dto.result)
    );
  }
  constructor(
    public request: AnnotatingRequestDTO | undefined,
    public result: AnnotatingResult | undefined
  ) {}
}

export class AuditLogDTO {
  static fromDTO(dto: AuditLogDTO | undefined): AuditLogDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new AuditLogDTO(dto.id, dto.creationTime, dto.content, dto.userId);
  }
  static fromDTOs(dtos: AuditLogDTO[]): AuditLogDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => AuditLogDTO.fromDTO(dto))
      .filter((dto): dto is AuditLogDTO => !!dto);
  }
  constructor(
    public id: number,
    public creationTime: number,
    public content: string,
    public userId: number | undefined
  ) {}
}

export enum AppRole {
  ADMIN = 'ADMIN',
  USER = 'USER',
}

export class AppUserDTO {
  static fromDTO(dto: AppUserDTO | undefined): AppUserDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new AppUserDTO(dto.id, dto.emailAddress, dto.roles);
  }
  static fromDTOs(dtos: AppUserDTO[]): AppUserDTO[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => AppUserDTO.fromDTO(dto))
      .filter((dto): dto is AppUserDTO => !!dto);
  }
  constructor(
    public id: number,
    public emailAddress: string,
    public roles: AppRole[]
  ) {}
}

export class CreateUserRequestDTO {
  constructor(
    public emailAddress: string,
    public password: string,
    public roles: AppRole[]
  ) {}
}

export class PasswordChangeRequestDTO {
  constructor(
    public userId: number,
    public oldPassword: string,
    public newPassword: string
  ) {}
}

export class UpdateUserRequestDTO {
  constructor(
    public userId: number,
    public emailAddress: string,
    public roles: AppRole[]
  ) {}
}

export enum LadderField {
  ID = 'ID',
  ALT_ID = 'ALT_ID',
  LANGUAGE = 'LANGUAGE',
  TEXT_CONTENT = 'TEXT_CONTENT',
  CREATION_TASK = 'CREATION_TASK',
  LAST_MODIFIED = 'LAST_MODIFIED',
  SPEAKER_GENDER = 'SPEAKER_GENDER',
  AGE_AT_CREATION = 'AGE_AT_CREATION',
  L1_LANGUAGE = 'L1_LANGUAGE',
  L2_LANGUAGE = 'L2_LANGUAGE',
  LOCATION = 'LOCATION',
  MODIFIERS = 'MODIFIERS',
  SUBACTS = 'SUBACTS',
}

export enum ClauseMode {
  AND = 'AND',
  OR = 'OR',
}

export class SearchClause {
  static fromDTO(dto: SearchClause | undefined): SearchClause | undefined {
    if (!dto) {
      return undefined;
    }
    return new SearchClause(dto.mode, dto.field, dto.queryString);
  }
  static fromDTOs(dtos: SearchClause[]): SearchClause[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => SearchClause.fromDTO(dto))
      .filter((dto): dto is SearchClause => !!dto);
  }
  constructor(
    public mode: ClauseMode,
    public field: LadderField,
    public queryString: string
  ) {}
}

export class SearchRequest {
  static fromDTO(dto: SearchRequest | undefined): SearchRequest | undefined {
    if (!dto) {
      return undefined;
    }
    return new SearchRequest(SearchClause.fromDTOs(dto.clauses));
  }
  constructor(public clauses: SearchClause[]) {}
}

export class SearchHit {
  static fromDTO(dto: SearchHit | undefined): SearchHit | undefined {
    if (!dto) {
      return undefined;
    }
    return new SearchHit(
      TextWithMetadataDTO.fromDTO(dto.textMetadata),
      dto.snippets
    );
  }
  static fromDTOs(dtos: SearchHit[]): SearchHit[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => SearchHit.fromDTO(dto))
      .filter((dto): dto is SearchHit => !!dto);
  }
  constructor(
    public textMetadata: TextWithMetadataDTO | undefined,
    public snippets: string[]
  ) {}
}

export class SearchResultDTO {
  static fromDTO(
    dto: SearchResultDTO | undefined
  ): SearchResultDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new SearchResultDTO(
      SearchRequest.fromDTO(dto.searchRequest),
      dto.totalHits,
      SearchHit.fromDTOs(dto.hits)
    );
  }
  constructor(
    public searchRequest: SearchRequest | undefined,
    public totalHits: number,
    public hits: SearchHit[]
  ) {}
}

export enum ModelType {
  MODIFIER = 'MODIFIER',
  SUBACT = 'SUBACT',
}

export class ModelMessage {
  static fromDTO(dto: ModelMessage | undefined): ModelMessage | undefined {
    if (!dto) {
      return undefined;
    }
    return new ModelMessage(
      dto.type,
      dto.modelName,
      dto.language,
      dto.messages
    );
  }
  static fromDTOs(dtos: ModelMessage[]): ModelMessage[] {
    if (!dtos) {
      return [];
    }
    return dtos
      .map((dto) => ModelMessage.fromDTO(dto))
      .filter((dto): dto is ModelMessage => !!dto);
  }
  constructor(
    public type: ModelType,
    public modelName: string,
    public language: string,
    public messages: string[]
  ) {}
}

export class RetrainModelMessagesDTO {
  static fromDTO(
    dto: RetrainModelMessagesDTO | undefined
  ): RetrainModelMessagesDTO | undefined {
    if (!dto) {
      return undefined;
    }
    return new RetrainModelMessagesDTO(
      ModelMessage.fromDTOs(dto.modelMessageList)
    );
  }
  constructor(public modelMessageList: ModelMessage[]) {}
}

export class SubactDisplay {
  constructor(public main: SubactDTO, public subs: SubactDTO[]) {}
}
