package org.kotlinlsp

import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.TextDocumentItem
import org.junit.jupiter.api.Test
import org.kotlinlsp.setup.scenario
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito.verify

class RealTimeDiagnostics {
    @Test
    fun `analyzes basic codebase with no error diagnostics`() = scenario("playground") { server, client, projectUrl, _ ->
        // Act
        server.didOpen(DidOpenTextDocumentParams().apply {
            textDocument = TextDocumentItem().apply {
                uri = "$projectUrl/Main.kt"
            }
        })

        // Assert
        verify(client).publishDiagnostics(argThat { !it.diagnostics.any { it.severity == DiagnosticSeverity.Error } })
    }

    @Test
    fun `analyzes basic codebase and reports syntax error`() = scenario("playground") { server, client, projectUrl, _ ->
        // Act
        server.didOpen(DidOpenTextDocumentParams().apply {
            textDocument = TextDocumentItem().apply {
                uri = "$projectUrl/Errors.kt"
            }
        })

        // Assert
        verify(client).publishDiagnostics(argThat {
            it.diagnostics.any {
                it.severity == DiagnosticSeverity.Error && it.message == "Expecting a top level declaration"
            }
        })
    }
}
