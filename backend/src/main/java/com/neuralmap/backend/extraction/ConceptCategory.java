package com.neuralmap.backend.extraction;

/**
 * The allowed concept categories: the PRD list (FR-03) plus METHOD and OTHER.
 * The model must pick one of these (they are sent as an enum in the JSON schema).
 */
public enum ConceptCategory {
    DEFINITION,
    THEORY,
    ALGORITHM,
    MATHEMATICAL_CONCEPT,
    DATA_STRUCTURE,
    TECHNOLOGY,
    PRINCIPLE,
    EXAMPLE,
    METHOD,
    OTHER;

    /** The spelling used in the prompt and the JSON schema, e.g. "data_structure". */
    public String jsonValue() {
        return name().toLowerCase();
    }
}
