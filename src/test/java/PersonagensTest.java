import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonagensTest {

    private ObjectMapper mapper = new ObjectMapper();

    private static Integer idInserido = null;

    @BeforeAll
    public static void preCondicao() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api/personagens";
    }

    @Test
    @Order(1)
    @DisplayName("Tentativa de criar personagens com dados invválidos")
    public void criarPersonagem_invalido() throws JsonProcessingException {
        var objNode = mapper.createObjectNode()
                .put("cpf", 123L).put("serie", "Aneis do Poder");
        String json = mapper.writeValueAsString(objNode);
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("nome", notNullValue());
    }

    @Test
    @Order(2)
    @DisplayName("Criar personagens com dados válidos")
    public void criarPersonagem_valido() throws JsonProcessingException {
        var objNode = mapper.createObjectNode()
                .put("cpf", 123L).put("nome", "Galadriel")
                .put("dataNascimento", "20-01-1900")
                .put("serie", "Aneis do Poder");
        String json = mapper.writeValueAsString(objNode);
        Integer id = given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", notNullValue())
                .extract().path("id");
        idInserido = id;
    }

    @Test
    @Order(3)
    @DisplayName("Consultar lista de personagens")
    public void consultarPersonagens() throws JsonProcessingException {
        given()
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", is(1));
    }

    @Test
    @Order(4)
    @DisplayName("Consultar personagem pelo identificador")
    public void consultarPersonagem() throws JsonProcessingException {
        given()
                .when()
                .get("{id}", idInserido)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("cpf", is(123))
                .body("nome", is("Galadriel"));
    }

    @Test
    @Order(5)
    @DisplayName("Excluir personagem pelo identificador")
    public void excluirPersonagem() throws JsonProcessingException {
        given()
                .when()
                .delete("{id}", idInserido)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Order(6)
    @DisplayName("Consultar lista de personagens após exclusao")
    public void consultarPersonagens_exclusao() throws JsonProcessingException {
        given()
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", is(0));
    }

}