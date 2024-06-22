package net.proselyte.qafordevs.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.proselyte.qafordevs.dto.DeveloperDto;
import net.proselyte.qafordevs.entity.DeveloperEntity;
import net.proselyte.qafordevs.repository.DeveloperRepository;
import net.proselyte.qafordevs.util.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItDeveloperRestControllerV1Tests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeveloperRepository developerRepository;

    @BeforeEach
    void setUp() {
        developerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create developer functionality")
    public void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DataUtils.getJonhDoeDtoTransient();

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("Jonh")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test create developer with duplicate email functionality")
    public void givenDeveloperDtoWithDuplicateEmail_whenCreateDeveloper_thenErrorResponse() throws Exception {
        //given
        String duplicateEmail = "duplicate@mail.com";
        DeveloperEntity developer = DataUtils.getJonhDoeTransient();
        developer.setEmail(duplicateEmail);
        developerRepository.save(developer);
        DeveloperDto dto = DataUtils.getJonhDoeDtoTransient();
        dto.setEmail(duplicateEmail);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer with defined email is already exists")));
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperDto_whenUpdateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DataUtils.getJonhDoeDtoPersisted();
        dto.setFirstName("John");
        DeveloperEntity entity = DataUtils.getJonhDoePersisted();
        developerRepository.save(entity);
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test update developer which not exist functionality")
    public void givenDeveloperDto_whenUpdateDeveloper_thenErrorResponse() throws Exception {
        //given
        DeveloperDto dto = DataUtils.getJonhDoeDtoPersisted();
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenId_whenFindById_thenSuccessResponse() throws Exception {
        //given
        DeveloperEntity entity = DataUtils.getJonhDoePersisted();
        developerRepository.save(entity);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("Jonh")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test get developer by wrong id functionality")
    public void givenWrongId_whenFindById_thenErrorResponse() throws Exception {
        //given
        DeveloperEntity entity = DataUtils.getJonhDoePersisted();
        developerRepository.save(entity);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/" + 2)
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }

    @Test
    @DisplayName("Test get all developers functionality")
    public void givenListOfEmployees_whenFindAll_thenSuccessResponse() throws Exception {
        //given
        List<DeveloperEntity> developerEntityList = new ArrayList<>(List.of(
                DataUtils.getJonhDoePersisted(),
                DataUtils.getFrankJonesPersisted(),
                DataUtils.getMikeSmithPersisted()
        ));
        developerRepository.saveAll(developerEntityList);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName", CoreMatchers.is("Jonh")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName",CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName", CoreMatchers.is("Mike")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName",CoreMatchers.is("Smith")));
    }
    @Test
    @DisplayName("Test get all developers by specialty functionality")
    public void givenListOfEmployeesAndSpecialty_whenFindAll_thenSuccessResponse() throws Exception {
        //given
        List<DeveloperEntity> developerEntityList = new ArrayList<>(List.of(
                DataUtils.getJonhDoePersisted(),
                DataUtils.getMikeSmithPersisted(),
                DataUtils.getFrankJonesPersisted()
        ));
        String specialty = "Java";

        developerRepository.saveAll(developerEntityList);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/specialty/" + specialty)
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName", CoreMatchers.is("Jonh")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName",CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialty",CoreMatchers.is("Java")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName", CoreMatchers.is("Mike")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName",CoreMatchers.is("Smith")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specialty",CoreMatchers.is("Java")));

    }

    @Test
    @DisplayName("Test soft delete developer vy id functionality")
    public void givenId_whenSoftDelete_thenSuccessResponse() throws Exception {
        //given
        developerRepository.save(DataUtils.getJonhDoeTransient());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + 1)
                .contentType(MediaType.APPLICATION_JSON)

        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test soft delete developer by incorrect id functionality")
    public void givenIncorrectId_whenSoftDelete_thenErrorResponse() throws Exception {
        //given
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + 1)
                .contentType(MediaType.APPLICATION_JSON)

        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }

    @Test
    @DisplayName("Test hard delete developer by id functionality")
    public void givenId_whenHardDelete_thenSuccessResponse() throws Exception {
        //given
        developerRepository.save(DataUtils.getJonhDoeTransient());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + 1 + "?isHard=true")
                .contentType(MediaType.APPLICATION_JSON)

        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test soft delete developer by incorrect id functionality")
    public void givenIncorrectId_whenHardDelete_thenErrorResponse() throws Exception {
        //given
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + 1 + "?isHard=true")
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }
}