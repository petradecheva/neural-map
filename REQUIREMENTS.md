# NeuralMap
## Product Requirements Document (PRD)

**Version:** 1.0  
**Status:** Initial product specification  
**Target:** Functional MVP for a late-November 2026 hackathon  
**Product type:** AI-powered educational platform  
**Initial platform:** Local desktop/laptop web application

---

# 1. Product Overview

## 1.1 Product Description

NeuralMap is an AI-powered learning platform that transforms unstructured educational materials into interactive knowledge graphs.

Students frequently consume information through lecture slides, PDFs, textbooks, presentations, and notes. However, these materials often present concepts independently, making it difficult to understand how different topics relate to one another.

NeuralMap addresses this problem by extracting concepts and their relationships from educational materials and organising them into an interactive visual representation of knowledge.

Users can explore concepts, understand relationships, ask contextual questions, access original sources, and eventually receive personalised learning recommendations.

The long-term vision is to create a personal knowledge system that helps students understand not only individual concepts but also how their knowledge connects across subjects and courses.

## 1.2 Product Vision

To make learning more connected, visual, interactive, and personalised.

NeuralMap should evolve from a document-to-knowledge-graph tool into an adaptive learning environment that understands a student's learning materials and helps them navigate their knowledge.

## 1.3 Core Value Proposition

**Turn scattered learning materials into connected, explorable knowledge.**

NeuralMap helps students:

- Understand complex topics and their underlying structure.
- Discover relationships between concepts.
- Navigate unfamiliar subjects more effectively.
- Access explanations grounded in their original learning materials.
- Identify what they should learn next.

---

# 2. Problem Statement

## 2.1 The Problem

University students work with large amounts of educational material distributed across different documents and courses.

Traditional learning tools generally focus on storing, displaying, or summarising information rather than representing the relationships between concepts.

This creates several problems:

1. **Fragmented knowledge:** Concepts are often studied independently, even when they depend on or relate to one another.
2. **Difficult navigation:** Students struggle to understand how large topics are organised.
3. **Limited contextual understanding:** Traditional summaries condense information but do not necessarily explain the connections between concepts.
4. **Unclear learning priorities:** Students may not know which foundational concepts they need to understand before progressing.
5. **Disconnected courses:** Knowledge acquired in different subjects is rarely connected automatically.

## 2.2 Proposed Solution

NeuralMap processes educational documents and constructs an interactive knowledge graph.

Instead of presenting information as a linear document, it represents knowledge through:

- **Nodes:** Individual concepts, topics, definitions, algorithms, theories, and other knowledge units.
- **Edges:** Relationships and dependencies between concepts.
- **Sources:** References to the original documents and pages supporting extracted information.

The resulting graph becomes an interactive learning environment rather than a static visualisation.

---

# 3. Target Audience

## 3.1 Primary Users

University students who regularly work with:

- Lecture slides.
- PDF documents.
- Course notes.
- Textbooks.
- Research papers.
- Technical documentation.

Initial focus: students studying information systems, computer science, mathematics, engineering, and other subjects with interconnected concepts.

## 3.2 Secondary Users

Potential future audiences:

- Researchers organising academic literature.
- Self-learners studying complex subjects.
- Professionals learning new technical domains.
- Teachers organising educational content.

## 3.3 User Personas

### Persona A: The University Student

Needs to understand large amounts of lecture material and prepare for exams.

Goals:
- Understand complex subjects.
- Identify important concepts.
- Discover connections between topics.
- Prepare efficiently for examinations.

### Persona B: The Independent Learner

Studies several subjects independently and wants to connect information from different resources.

Goals:
- Build a personal knowledge base.
- Discover relationships across subjects.
- Explore topics beyond individual documents.

### Persona C: The Research-Oriented Student

Works with multiple academic documents and needs to understand how ideas relate.

Goals:
- Organise research materials.
- Explore relationships between theories and concepts.
- Trace information back to original sources.

---

# 4. Product Scope

NeuralMap will be developed incrementally.

## 4.1 MVP Scope — Hackathon Version

The MVP must demonstrate the complete core workflow:

1. Upload a PDF.
2. Extract document text.
3. Use an LLM to identify concepts.
4. Extract relationships between concepts.
5. Generate a structured knowledge graph.
6. Visualise the graph interactively.
7. Select concepts to view their details.
8. Access source references.
9. Ask questions about concepts using the document context.

A basic prerequisite-aware learning path is a stretch goal.

## 4.2 Future Scope

Features intentionally excluded from the initial MVP:

- Cross-course knowledge graphs.
- Multiple document collections.
- User accounts and authentication.
- Cloud synchronisation.
- Collaborative graphs.
- Advanced knowledge assessment.
- Personalised learning recommendations.
- Spaced repetition.
- Automated quizzes and examinations.
- Moodle integration.
- Mobile and desktop-native applications.
- Subscription and payment systems.

---

# 5. Functional Requirements

## FR-01: Document Management

### Description

Users must be able to upload educational PDF documents for processing.

### Requirements

- The system must support PDF uploads.
- The system must validate uploaded file types.
- The system must reject unsupported or corrupted files with a clear error.
- The system must extract the document name and metadata where available.
- Users must be able to view their uploaded documents.
- Users must be able to select a document for processing.
- Users must be able to remove documents.

### MVP Acceptance Criteria

- A user can upload a PDF successfully.
- The backend receives the file.
- The system can extract text from the document.
- The frontend displays the document and its processing status.
- Upload errors are communicated clearly.

## FR-02: PDF Text Extraction

### Description

The application must extract text from uploaded PDFs while preserving page-level information.

### Requirements

- Extract text from individual pages.
- Preserve page numbers.
- Maintain the association between extracted text and the original document.
- Handle empty pages.
- Detect documents with little or no extractable text.
- Return extraction errors where appropriate.

### MVP Acceptance Criteria

- Extracted text can be accessed by the AI processing pipeline.
- Every extracted page retains its original page number.
- Source references can be associated with extracted pages.

### Future Considerations

OCR support for scanned PDFs and image-based documents.

## FR-03: AI Concept Extraction

### Description

The AI must identify meaningful concepts within the extracted educational material.

### Requirements

For each concept, the system should extract:

- Concept name.
- Short definition.
- Detailed explanation, when available.
- Concept category.
- Supporting evidence.
- Source document.
- Source page number.

Possible concept categories:

- Definition.
- Theory.
- Algorithm.
- Mathematical concept.
- Data structure.
- Technology.
- Principle.
- Example.

The system must return structured data compatible with the application's graph model.

### MVP Acceptance Criteria

- The AI identifies concepts from extracted lecture text.
- Concepts are returned in a predefined JSON structure.
- Invalid or incomplete responses are detected.
- Duplicate concepts within a document are handled.
- Concepts retain source references wherever available.

## FR-04: AI Relationship Extraction

### Description

The AI must identify meaningful relationships between extracted concepts.

### Requirements

Each relationship should contain:

- Source concept.
- Target concept.
- Relationship type.
- Explanation.
- Supporting evidence.
- Source document and page reference.

Initial relationship types:

- RELATED_TO
- DEPENDS_ON
- IS_A
- PART_OF
- EXAMPLE_OF
- USED_BY
- PRODUCES
- IMPLEMENTS

Relationship types should be extensible.

The AI must not create relationships solely because two concepts appear in the same document.

### MVP Acceptance Criteria

- The system identifies relationships between extracted concepts.
- Each relationship references existing concepts.
- Relationships contain an explanation and supporting evidence.
- Invalid references are rejected.
- Relationships can be represented as graph edges.

## FR-05: Knowledge Graph Generation

### Description

The system must combine extracted concepts and relationships into a structured knowledge graph.

### Requirements

- Represent concepts as nodes.
- Represent relationships as directed or undirected edges, depending on relationship type.
- Assign unique identifiers to nodes and edges.
- Avoid duplicate concepts and relationships.
- Preserve source references.
- Support graph retrieval through the backend API.

### MVP Acceptance Criteria

- A valid graph is generated from processed document data.
- Nodes and edges have stable identifiers.
- All edges reference existing nodes.
- The graph can be returned to the frontend in a consistent format.

## FR-06: Interactive Graph Visualisation

### Description

Users must be able to visually explore the generated knowledge graph.

### Requirements

- Display concepts as nodes.
- Display relationships as connecting edges.
- Display concept labels.
- Allow users to select nodes.
- Allow users to move nodes.
- Support zooming and panning.
- Highlight selected nodes.
- Highlight directly connected concepts.
- Allow users to reset or reposition the graph view.

### MVP Acceptance Criteria

- The graph is rendered from backend data.
- Nodes and edges are interactive.
- Selecting a node displays its details.
- The graph remains navigable with a reasonable number of concepts.

## FR-07: Concept Details

### Description

Selecting a node must provide additional information about the concept.

### Requirements

The concept details panel should display:

- Concept name.
- Definition.
- Detailed explanation.
- Category.
- Related concepts.
- Source document.
- Source page number.
- Supporting evidence.

### MVP Acceptance Criteria

- Selecting a node opens a details panel.
- The correct concept information is displayed.
- Source information is accessible.

## FR-08: Relationship Details

### Description

Users must be able to inspect why two concepts are connected.

### Requirements

- Selecting an edge displays its relationship type.
- Display an explanation of the relationship.
- Display supporting evidence.
- Display source references.

### MVP Acceptance Criteria

- Users can select a relationship.
- The application explains the connection between the two concepts.

## FR-09: Contextual AI Assistant

### Description

Users must be able to ask questions about concepts and receive answers grounded in their uploaded learning materials.

### Requirements

- Provide a chat interface.
- Allow questions about the selected concept.
- Retrieve relevant document passages.
- Include relevant concept and relationship context.
- Generate explanations using the local LLM.
- Indicate relevant source pages.
- Communicate when the available materials do not support an answer.

Example questions:

- Explain this concept in simpler terms.
- Why is this concept important?
- How does this relate to another concept?
- Give me an example.
- What should I understand before learning this?
- Explain the relationship between these two concepts.

### MVP Acceptance Criteria

- Users can submit questions.
- The backend retrieves relevant context.
- Ollama generates a response.
- The response is displayed in the frontend.
- Unsupported claims are not presented as verified document content.

## FR-10: Prerequisite-Aware Learning Paths

### Description

The system should eventually help students determine the order in which concepts should be studied.

### Requirements

- Identify prerequisite relationships.
- Allow users to select a target concept.
- Find prerequisite chains.
- Generate an ordered learning path.
- Explain why each prerequisite is relevant.

Example:

Target: Dijkstra's Algorithm

Suggested learning path:

1. Graph theory.
2. Weighted graphs.
3. Paths and cycles.
4. Priority queues.
5. Greedy algorithms.
6. Dijkstra's algorithm.

### MVP Status

Stretch goal. Implement only after the core graph workflow is reliable.

---

# 6. Non-Functional Requirements

## NFR-01: Performance

- The application should remain responsive during normal graph exploration.
- AI processing may take longer, but the interface must communicate processing status.
- Long-running document processing must not block unrelated application interactions.
- The system should handle typical university lecture PDFs.

## NFR-02: Reliability

- Validate AI-generated structured data before storing it.
- Handle model failures and malformed responses.
- Handle PDF extraction errors.
- Avoid saving incomplete graphs as successfully processed graphs.
- Provide clear error messages.

## NFR-03: Privacy

- The initial application should operate locally on the user's computer.
- Uploaded files should remain in local storage.
- Ollama should be used for local inference.
- Lecture documents should not be sent to external AI providers by default.
- The application should not transmit user documents to external services without explicit user configuration.

## NFR-04: Maintainability

- Separate frontend and backend responsibilities.
- Use clear service, controller, repository, and model layers.
- Keep AI integration behind an abstraction.
- Use typed DTOs and structured API responses.
- Avoid tightly coupling application logic to a particular LLM provider.

## NFR-05: Extensibility

The architecture should allow future support for:

- Additional LLM providers.
- Different document formats.
- Multiple courses.
- Cross-document knowledge graphs.
- Vector search.
- User accounts.
- Cloud synchronisation.
- Advanced learning analytics.

## NFR-06: Usability

- The interface should be intuitive for students without technical experience.
- Graph interactions should be discoverable.
- Processing states should be visible.
- Errors should provide actionable information.
- Concept details should be accessible without leaving the graph.

---

# 7. User Stories

| ID | User Story | Priority |
|---|---|---|
| US-01 | As a student, I want to upload a lecture PDF so that NeuralMap can process my learning material. | Must |
| US-02 | As a student, I want the system to extract important concepts so that I can understand the structure of the material. | Must |
| US-03 | As a student, I want to see how concepts relate to each other so that I can understand the bigger picture. | Must |
| US-04 | As a student, I want to explore an interactive graph so that I can navigate the material visually. | Must |
| US-05 | As a student, I want to select a concept and see its definition so that I can understand it in more detail. | Must |
| US-06 | As a student, I want to see the original source of a concept so that I can verify the information. | Must |
| US-07 | As a student, I want to ask questions about a concept so that I can clarify difficult topics. | Should |
| US-08 | As a student, I want explanations of relationships so that I understand why concepts are connected. | Should |
| US-09 | As a student, I want to search for concepts so that I can quickly find information. | Could |
| US-10 | As a student, I want a learning path so that I know what to study before a difficult topic. | Could |
| US-11 | As a student, I want to test my understanding so that I can identify knowledge gaps. | Future |
| US-12 | As a student, I want to connect knowledge from multiple courses so that I can discover relationships across subjects. | Future |

---

# 8. AI Requirements and Behaviour

## 8.1 LLM Integration

The initial implementation should use Ollama with a locally hosted model.

The application must not depend directly on a specific model implementation.

An AI provider abstraction should allow future integration of alternative models.

## 8.2 Structured Outputs

AI extraction should return structured JSON.

The backend must validate the response against an expected schema before accepting it.

The system should handle:

- Invalid JSON.
- Missing required fields.
- Unknown relationship types.
- References to nonexistent concepts.
- Duplicate concepts.
- Unsupported claims.

## 8.3 Grounding and Source Attribution

Every extracted concept and relationship should retain its provenance.

The AI should provide supporting text from the source material wherever possible.

The system should distinguish between:

- Information explicitly stated in the document.
- Information inferred from the document.
- Explanations generated by the AI.

Generated explanations must not be represented as direct quotations from the source.

## 8.4 Document Processing Strategy

For the MVP:

1. Extract PDF text page by page.
2. Divide long documents into manageable chunks.
3. Process chunks individually.
4. Extract concepts and relationships.
5. Consolidate duplicate concepts.
6. Validate relationships.
7. Store the resulting graph.

Avoid sending entire long documents to the model in a single request.

---

# 9. User Interface Requirements

## 9.1 Main Application Layout

The interface should contain:

### Navigation Sidebar

- NeuralMap logo.
- Dashboard.
- Documents.
- Knowledge Graphs.
- Settings.

### Main Workspace

- Document upload.
- Current document or graph title.
- Interactive knowledge graph.
- Graph controls.

### Details Panel

- Selected concept information.
- Relationship explanations.
- Source references.

### AI Assistant Panel

- Chat input.
- Conversation history.
- Contextual questions.
- Generated answers.

The panels may be arranged differently depending on screen size and the selected view.

## 9.2 Visual Design Direction

The application should feel like a modern knowledge-management and productivity platform.

Design principles:

- Minimal and clean.
- Clear visual hierarchy.
- Intuitive navigation.
- Limited visual clutter.
- Readable graph labels.
- Distinct visual representation of concept categories.
- Clear distinction between concepts and relationships.

Avoid overly complex visual effects that interfere with graph readability.

---

# 10. Technical Constraints

## 10.1 Initial Environment

- Local laptop application.
- Windows development environment.
- 16 GB RAM.
- Local LLM inference through Ollama.
- No requirement for paid AI APIs.
- Internet connection may be required for initial setup and model downloads.

## 10.2 Technology Choices

| Component | Technology |
|---|---|
| Frontend | React |
| Frontend language | TypeScript |
| Build tool | Vite |
| Styling | Tailwind CSS |
| Graph library | React Flow |
| Backend | Java |
| Backend framework | Spring Boot |
| PDF extraction | Apache PDFBox |
| AI runtime | Ollama |
| Initial LLM | Qwen3.5 4B, subject to hardware testing |
| Database | PostgreSQL |
| Version control | Git / GitHub |

## 10.3 Architectural Principles

- Frontend and backend communicate through REST APIs.
- AI functionality is isolated behind an `AIProvider` abstraction.
- Document extraction is separate from AI processing.
- Graph data is represented using explicit nodes and edges.
- Database persistence is separate from graph visualisation.
- All AI outputs are validated before persistence.

---

# 11. Success Metrics

The MVP should be evaluated using measurable criteria.

## Functional Success

- A user can upload and process a PDF.
- A graph can be generated from the document.
- Concepts and relationships are displayed correctly.
- Selecting a node reveals its details.
- Source references are available.
- The user can ask contextual questions.

## Quality Success

For an initial evaluation, manually inspect a representative set of extracted concepts and relationships.

Measure:

- Percentage of extracted concepts judged relevant.
- Percentage of relationships judged correct.
- Percentage of relationships supported by source evidence.
- Duplicate concept frequency.
- Percentage of successful structured outputs.
- Document processing time.

The MVP should prioritise accuracy and traceability over the number of generated nodes.

## Product Success

A successful hackathon demonstration should show that:

1. NeuralMap solves a recognisable learning problem.
2. The core workflow works from beginning to end.
3. The knowledge graph is genuinely interactive.
4. AI-generated information is grounded in learning materials.
5. The product has a credible path towards personalised learning.

---

# 12. Development Priorities

## P0 — Essential

- Project setup.
- PDF upload.
- PDF text extraction.
- Ollama integration.
- Concept extraction.
- Relationship extraction.
- Graph data structure.
- Interactive graph.
- Concept details.

## P1 — Important

- Source references.
- Relationship explanations.
- Contextual AI assistant.
- Database persistence.
- Error handling.
- Basic graph search.

## P2 — Stretch Goals

- Prerequisite-aware learning paths.
- Concept categorisation.
- Graph filtering.
- Basic quizzes.

## P3 — Future Product

- Cross-course knowledge graphs.
- Knowledge mastery tracking.
- Personalised learning recommendations.
- Spaced repetition.
- Collaborative learning.
- Moodle integration.
- Cloud and mobile support.

---

# 13. Product Roadmap

### Phase 1 — MVP

**Objective:** Convert a single PDF into an interactive, sourced knowledge graph.

### Phase 2 — Learning Assistant

**Objective:** Enable contextual AI explanations, question answering, and quizzes.

### Phase 3 — Adaptive Learning

**Objective:** Understand a student's knowledge, identify gaps, and recommend learning paths.

### Phase 4 — Personal Knowledge System

**Objective:** Connect knowledge across multiple documents, courses, and subjects.

### Phase 5 — Platform Expansion

**Objective:** Support cloud synchronisation, mobile access, collaboration, and integrations with university systems.

---

# 14. Definition of Done — Hackathon MVP

The hackathon MVP is considered complete when:

- [ ] The application launches locally.
- [ ] A user can upload a PDF.
- [ ] PDF text extraction works.
- [ ] Ollama processes the extracted text.
- [ ] Concepts are extracted into structured data.
- [ ] Relationships are extracted into structured data.
- [ ] The backend validates the graph.
- [ ] The graph is displayed using React Flow.
- [ ] Users can select and explore concepts.
- [ ] Concept definitions and explanations are displayed.
- [ ] Source references are accessible.
- [ ] The contextual AI assistant works.
- [ ] The application handles common errors.
- [ ] A complete demonstration can be performed reliably.

**Final MVP objective:**

A student uploads a university lecture PDF and receives an interactive, AI-generated knowledge graph that explains the concepts, reveals their relationships, and allows the student to explore the material through contextual AI assistance.

---

# 15. Guiding Product Principle

NeuralMap should not simply generate more information for students.

It should help students understand how information fits together.

The value of the product is not the number of concepts extracted or the size of the graph. Its value is whether the student develops a clearer understanding of the subject.

**NeuralMap — See how knowledge connects.**