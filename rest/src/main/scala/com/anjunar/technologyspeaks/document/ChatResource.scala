package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.olama.json.{JsonFunctionBody, JsonFunction, JsonNode, JsonObject, NodeType}
import com.anjunar.technologyspeaks.olama.{ChatMessage, ChatRequest, ChatRole, EmbeddingRequest, OLlamaService}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.{GET, Path, QueryParam}

import scala.compiletime.uninitialized

@Path("chat")
@ApplicationScoped
class ChatResource {

  @Inject
  var oLlamaService: OLlamaService = uninitialized

  @Inject
  var documentService: DocumentAIService = uninitialized

  @GET
  def chat(@QueryParam("text") text: String): String = {

    val chatRequest = new ChatRequest
    chatRequest.model = "gemma3:12b"

    val chatMessage = new ChatMessage
    chatMessage.role = ChatRole.USER
    chatMessage.content = text

    val jsonNode = new JsonNode
    jsonNode.nodeType = NodeType.STRING
    jsonNode.description = "The natural language query to search for"

    val jsonObject = new JsonObject
    jsonObject.nodeType = NodeType.OBJECT
    jsonObject.properties.put("query", jsonNode)

    val function = new JsonFunctionBody
    function.name = "semantic_search_documents"
    function.description = "Performs semantic search over documents in a database and returns full original texts"
    function.parameters = jsonObject

    val functionWrapper = new JsonFunction
    functionWrapper.nodeType = NodeType.FUNCTION
    functionWrapper.function = function

    chatRequest.tools.add(functionWrapper)
    chatRequest.messages.add(chatMessage)

    val response = oLlamaService.chat(chatRequest)

    val toolCalls = response.message.toolCalls

    val document = toolCalls.stream
      .map(call => {
        val function = call.function

        val value = function.arguments.get("query")

        val vector = documentService.createEmbeddings(value.asInstanceOf[String])

        val chunks = documentService.findTop5Embeddings(vector)

        chunks.stream().findFirst().get().document
      })
      .findFirst()
      .get()

    val chatRequest2 = new ChatRequest
    chatRequest2.model = "gemma3:12b"

    val chatMessageAssistant = new ChatMessage
    chatMessageAssistant.role = ChatRole.ASSISTANT
    chatMessageAssistant.toolCalls.addAll(toolCalls)

    val chatMessageTool = new ChatMessage
    chatMessageTool.role = ChatRole.TOOL
    chatMessageTool.content = documentService.toText(document.editor.json)

    chatRequest2.messages.add(chatMessage)
    chatRequest2.messages.add(chatMessageAssistant)
    chatRequest2.messages.add(chatMessageTool)

    val response2 = oLlamaService.chat(chatRequest2)

    response2.message.content
  }

}
