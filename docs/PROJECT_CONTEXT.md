# NeuralMap — Project Context

## 1. Project Overview

**NeuralMap** is an AI-powered learning platform that transforms university lecture materials into interactive, interconnected knowledge graphs.

The goal is to help students understand not only individual concepts, but also how those concepts relate to one another, depend on each other, and fit into a larger subject.

The initial version focuses on converting lecture PDFs into structured, explorable knowledge graphs, with AI-generated explanations grounded in the original learning materials.

## 2. Product Vision

NeuralMap aims to become a personal learning environment that helps students:

- Organise knowledge from different university courses.
- Discover relationships between concepts.
- Understand difficult topics through contextual explanations.
- Identify prerequisite concepts and knowledge gaps.
- Navigate subjects through personalised learning paths.

The long-term vision is an adaptive AI learning assistant built around a student's personal knowledge graph.

## 3. Initial MVP

The first version should focus on the following workflow:

1. Upload a university lecture PDF.
2. Extract its text while preserving page references.
3. Use an AI model to identify important concepts.
4. Identify relationships between those concepts.
5. Generate a structured knowledge graph.
6. Display the graph in an interactive interface.
7. Allow users to select concepts and relationships to view explanations and supporting source references.
8. Eventually, allow users to ask questions about the uploaded material.

The MVP should demonstrate a complete and reliable PDF-to-knowledge-graph workflow.

Prioritise functionality and reliability over implementing every feature in the long-term vision.

## 4. Technology Stack

The preferred technology stack is:

### Frontend
- React
- TypeScript
- Vite
- React Flow for knowledge graph visualisation
- Tailwind CSS for styling

### Backend
- Java
- Spring Boot
- REST API architecture
- Apache PDFBox for PDF text extraction

### AI
- Ollama for running local language models.
- Initially investigate Qwen models, starting with a relatively small model suitable for the available hardware.
- Use structured JSON output for concept and relationship extraction.
- Design an AI provider abstraction so that other models or hosted APIs can be integrated later without rewriting the application.

### Database
- PostgreSQL for storing documents, concepts, relationships, and associated metadata.

### Development Tools
- VS Code
- Git and GitHub
- Claude Code as a development assistant

## 5. Architectural Direction

The application should follow a clear separation of responsibilities:

**Frontend → Backend API → Application Services → External/Local Services**

The frontend communicates with the Spring Boot backend through REST endpoints.

The backend is responsible for:
- Receiving and managing uploaded documents.
- Extracting text and preserving page numbers.
- Coordinating AI processing.
- Validating and structuring AI-generated data.
- Managing knowledge graphs and persistence.
- Providing source-grounded explanations and answers.

The AI integration should be isolated behind an interface, such as `AIProvider`, with an initial `OllamaAIProvider` implementation.

Avoid tightly coupling business logic to a particular AI model or provider.

## 6. Important Technical Principles

### Source Attribution and Reliability

Source attribution is a core feature, not an optional enhancement.

- Concepts should retain references to the documents and pages they originate from.
- Relationships should include supporting evidence where possible.
- AI-generated explanations should be grounded in the uploaded material.
- Never invent source references, quotations, or page numbers.
- Distinguish between explicitly stated relationships and AI-inferred relationships.
- Validate AI-generated structured data