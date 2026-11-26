package com.neuralmap.backend.extraction;

import java.util.List;

/** The whole JSON answer of the model: {"concepts": [ ... ]}. Used to read it with Jackson. */
public record RawConceptResponse(List<RawConcept> concepts) {
}
