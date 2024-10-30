package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.controllers.UCSBOrganizationController;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase{
    @MockBean
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @MockBean
    UserRepository userRepository;

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganization/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganization/all"))
                                .andExpect(status().is(200)); // logged
        }
        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsborganization?orgCode=BAL"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }
        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganization/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganization/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsborganization() throws Exception {

                // arrange

                UCSBOrganization chess = UCSBOrganization.builder()
                                .orgCode("CHS")
                                .orgTranslationShort("Chess Club")
                                .orgTranslation("UCSB Chess Club")
                                .inactive(false)
                                .build();

                UCSBOrganization basketball = UCSBOrganization.builder()
                                .orgCode("BAL")
                                .orgTranslationShort("Basketball Club")
                                .orgTranslation("UCSB Basketball Club")
                                .inactive(false)
                                .build();

                ArrayList<UCSBOrganization> expectedOrganizations = new ArrayList<>();
                expectedOrganizations.addAll(Arrays.asList(chess, basketball));

                when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrganizations);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrganizations);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_organization() throws Exception {
                // arrange

                UCSBOrganization basketball = UCSBOrganization.builder()
                                .orgCode("BAL")
                                .orgTranslationShort("Basketball Club")
                                .orgTranslation("UCSB Basketball Club")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationRepository.save(eq(basketball))).thenReturn(basketball);

                // act
                MvcResult response = mockMvc.perform(
                                //post("/api/ucsborganization/post?name=Ortega&code=ortega&hasSackMeal=true&hasTakeOutMeal=true&hasDiningCam=true&latitude=34.410987&longitude=-119.84709")
                                post("/api/ucsborganization/post?orgCode=BAL&orgTranslationShort=Basketball Club&orgTranslation=UCSB Basketball Club&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).save(basketball);
                String expectedJson = mapper.writeValueAsString(basketball);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
         @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBOrganization organization = UCSBOrganization.builder()
                                .orgCode("BAL")
                                .orgTranslationShort("Basketball Club")
                                .orgTranslation("UCSB Basketball Club")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("BAL"))).thenReturn(Optional.of(organization));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=BAL"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("BAL"));
                String expectedJson = mapper.writeValueAsString(organization);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_organizations() throws Exception {
                // arrange

                UCSBOrganization bballOG = UCSBOrganization.builder()
                                .orgCode("BAL")
                                .orgTranslationShort("Basketball Club")
                                .orgTranslation("UCSB Basketball Club")
                                .inactive(false)
                                .build();

                UCSBOrganization bball = UCSBOrganization.builder()
                                .orgCode("NBAL")
                                .orgTranslationShort("New Basketball Club")
                                .orgTranslation("New UCSB Basketball Club")
                                .inactive(true)
                                .build();

                String requestBody = mapper.writeValueAsString(bball);

                when(ucsbOrganizationRepository.findById(eq("BAL"))).thenReturn(Optional.of(bballOG));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganization?orgCode=BAL")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("BAL");
                verify(ucsbOrganizationRepository, times(1)).save(bball); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_commons_that_does_not_exist() throws Exception {
                // arrange

                UCSBOrganization newOrganization = UCSBOrganization.builder()
                                .orgCode("BAL")
                                .orgTranslationShort("Basketball Club")
                                .orgTranslation("UCSB Basketball Club")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(newOrganization);

                when(ucsbOrganizationRepository.findById(eq("BAL"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganization?orgCode=BAL")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("BAL");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id BAL not found", json.get("message"));

        }
        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbOrganizationRepository.findById(eq("BAL"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=BAL"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("BAL"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganization with id BAL not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_an_organization() throws Exception {
                // arrange

                UCSBOrganization newOrganization = UCSBOrganization.builder()
                                .orgCode("BAL")
                                .orgTranslationShort("Basketball Club")
                                .orgTranslation("UCSB Basketball Club")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("BAL"))).thenReturn(Optional.of(newOrganization));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganization?orgCode=BAL")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("BAL");
                verify(ucsbOrganizationRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id BAL deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_organization_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationRepository.findById(eq("soccer"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganization?orgCode=soccer")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("soccer");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id soccer not found", json.get("message"));
        }
}
