import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreApiTests {

    @Test
    public void testCreateOrder() {
        // JSON com os dados do pedido de um pet
        String orderJson = "{"
            + "\"id\": 1,"  // ID do pedido
            + "\"petId\": 1,"  // ID do pet associado ao pedido
            + "\"quantity\": 1,"  // Quantidade do pet solicitado
            + "\"shipDate\": \"2024-07-27T00:00:00.000Z\","  // Data de envio
            + "\"status\": \"placed\","  // Status do pedido
            + "\"complete\": true"  // Indica se o pedido está completo
            + "}";

        // Envia uma requisição POST para criar um novo pedido de pet
        Response response = RestAssured
            .given()  // Inicia a construção da requisição
            .contentType(ContentType.JSON)  // Define o tipo de conteúdo como JSON
            .body(orderJson)  // Define o corpo da requisição com o JSON do pedido
            .when()  // Indica que a requisição será enviada agora
            .post("https://petstore.swagger.io/v2/store/order")  // Especifica o método HTTP e a URL da API
            .then()  // Inicia a verificação da resposta
            .statusCode(200)  // Verifica se o status da resposta é 200 (OK)
            .extract().response();  // Extrai a resposta completa para análise posterior

        // Verifica se o ID e o status do pedido na resposta são os esperados
        Assert.assertEquals(1, response.jsonPath().getInt("id"));
        Assert.assertEquals("placed", response.jsonPath().getString("status"));
    }

    @Test
    public void testFindNonExistentPet() {
        int nonExistentPetId = 999999;

        // Envia uma requisição GET para buscar um pet que não existe
        RestAssured
            .given()  // Inicia a construção da requisição
            .when()  // Indica que a requisição será enviada agora
            .get("https://petstore.swagger.io/v2/pet/" + nonExistentPetId)  // Especifica o método HTTP, a URL da API e o ID do pet
            .then()  // Inicia a verificação da resposta
            .statusCode(404);  // Verifica se o status da resposta é 404 (Not Found)
    }

    @Test
    public void testUpdatePet() {
        // JSON com os dados atualizados do pet
        String petJson = "{"
            + "\"id\": 1,"  // ID do pet que será atualizado
            + "\"name\": \"UpdatedPetName\","  // Novo nome para o pet
            + "\"status\": \"available\""  // Novo status para o pet
            + "}";

        // Envia uma requisição PUT para atualizar os dados de um pet existente
        RestAssured
            .given()  // Inicia a construção da requisição
            .contentType(ContentType.JSON)  // Define o tipo de conteúdo como JSON
            .body(petJson)  // Define o corpo da requisição com o JSON do pet
            .when()  // Indica que a requisição será enviada agora
            .put("https://petstore.swagger.io/v2/pet")  // Especifica o método HTTP e a URL da API
            .then()  // Inicia a verificação da resposta
            .statusCode(200);  // Verifica se o status da resposta é 200 (OK)
    }

    @Test
    public void testFindPetsByStatusPending() {
        // Envia uma requisição GET para buscar pets com status "pending"
        Response response = RestAssured
            .given()  // Inicia a construção da requisição
            .queryParam("status", "pending")  // Define o parâmetro de consulta para buscar pets "pending"
            .when()  // Indica que a requisição será enviada agora
            .get("https://petstore.swagger.io/v2/pet/findByStatus")  // Especifica o método HTTP e a URL da API
            .then()  // Inicia a verificação da resposta
            .statusCode(200)  // Verifica se o status da resposta é 200 (OK)
            .extract().response();  // Extrai a resposta completa para análise posterior

        // Verifica se todos os pets retornados têm o status "pending"
        response.jsonPath().getList("status").forEach(status -> Assert.assertEquals("pending", status));
    }
}
