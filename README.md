NeuralMap 

Turn your lecture materials into knowledge you can explore.

NeuralMap is an AI-powered learning platform that transforms university lecture materials into interactive knowledge graphs. Instead of navigating isolated notes and lengthy PDFs, students can explore concepts, understand how ideas connect, and discover the relationships behind what they learn.

Features

Core features

- PDF Upload: Import university lecture materials directly into the platform.
- AI-Powered Concept Extraction: Identify key concepts and definitions from lecture content.
- Relationship Mapping: Discover connections and dependencies between concepts.
- Interactive Knowledge Graph: Explore concepts and their relationships visually.
- Source References: Trace extracted concepts back to their original document and page.
- Concept Exploration: Select graph nodes to inspect definitions, explanations, and related concepts.

Planned features

- Context-aware AI assistant for asking questions about lecture materials.
- Learning paths based on concept dependencies.
- Cross-document knowledge graphs connecting multiple lectures and courses.
- Personalized learning recommendations.


Tech Stack

Layer| Technology
Backend| Java, Spring Boot
Frontend| React, TypeScript, Vite
AI / LLM| Ollama
PDF Processing| Apache PDFBox
Graph Visualization| React Flow
Database| Planned

The stack and implementation details may evolve during development.

Architecture

NeuralMap follows a client-server architecture:

                  ┌──────────────────────┐
                  │     React Frontend   │
                  │                      │
                  │  PDF Upload          │
                  │  Interactive Graph   │
                  │  Concept Exploration │
                  └──────────┬───────────┘
                             │ REST API
                             ▼
                  ┌──────────────────────┐
                  │   Spring Boot API    │
                  │                      │
                  │  Document Processing│
                  │  Graph Generation    │
                  │  Data Validation     │
                  └──────────┬───────────┘
                             │
                  ┌──────────┴───────────┐
                  ▼                      ▼
          ┌──────────────┐      ┌────────────────┐
          │  Apache      │      │  Ollama        │
          │  PDFBox      │      │  Local LLM     │
          │              │      │                │
          │ Text         │      │ Concept &      │
          │ Extraction   │      │ Relationship  │
          │              │      │ Extraction     │
          └──────────────┘      └────────────────┘

How it works

1. A student uploads a lecture PDF.
2. NeuralMap extracts the document text while preserving page references.
3. The LLM analyses the extracted content and identifies concepts and relationships.
4. The backend validates and structures the generated data.
5. The frontend transforms the structured data into an interactive knowledge graph.
6. Students explore concepts and trace them back to their original learning materials.

Getting Started

NeuralMap is currently under development. Setup instructions will be expanded as the application becomes runnable end-to-end.

Prerequisites

- Java JDK
- Maven
- Node.js and npm
- Ollama
- An Ollama model compatible with the configured application

Installation

1. Clone the repository

git clone https://github.com/petradecheva/neuralmap.git
cd neuralmap

2. Start Ollama

Install Ollama from "ollama.com" (https://ollama.com/) and download a compatible model:

ollama pull YOUR_MODEL

3. Start the backend

cd backend
./mvnw spring-boot:run

4. Start the frontend

cd frontend
npm install
npm run dev

Note: Commands and configuration may change as development progresses. Replace placeholders and add any required environment variables before publishing.

Roadmap

- [x] Initial Spring Boot backend
- [x] Initial PDF reading and text extraction
- [ ] Ollama integration
- [ ] AI-powered concept extraction
- [ ] Relationship extraction and validation
- [ ] React + TypeScript frontend
- [ ] Interactive knowledge graph
- [ ] Concept details and source references
- [ ] End-to-end PDF-to-graph workflow
- [ ] Contextual AI assistant
- [ ] Personalized learning paths
- [ ] Cross-course knowledge mapping
