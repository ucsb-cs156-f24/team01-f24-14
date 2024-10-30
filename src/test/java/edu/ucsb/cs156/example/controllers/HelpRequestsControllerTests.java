package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequests;
import edu.ucsb.cs156.example.repositories.HelpRequestsRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;

@WebMvcTest(controllers = HelpRequestsController.class)
@Import(TestConfig.class)
public class HelpRequestsControllerTests extends ControllerTestCase {

        @MockBean
        HelpRequestsRepository helpRequestsRepository; 

        @MockBean
        UserRepository userRepository;
        

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/HelpRequests/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/HelpRequests/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/HelpRequests/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/HelpRequests/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests helpRequest = HelpRequests.builder()
                                .requesterEmail("jenniferzhu@gmail.com")
                                .teamId("14")
                                .requestTime(ldt)
                                .tableOrBreakoutRoom("table 14")
                                .explanation("team01")
                                .solved(false)
                                .build();

                when(helpRequestsRepository.findById(eq(7L))).thenReturn(Optional.of(helpRequest));

                // act
                MvcResult response = mockMvc.perform(get("/api/HelpRequests?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(helpRequestsRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(helpRequest);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(helpRequestsRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/HelpRequests?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(helpRequestsRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("HelpRequests with id 7 not found", json.get("message"));
        }

         @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_helprequests() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests helpRequest1 = HelpRequests.builder()
                                .requesterEmail("jenniferzhu@gmail.com")
                                .teamId("14")
                                .requestTime(ldt1)
                                .tableOrBreakoutRoom("table 14")
                                .explanation("team01")
                                .solved(true)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                HelpRequests helpRequest2 = HelpRequests.builder()
                                .requesterEmail("ehehhahaha@gmail.com")
                                .teamId("05")
                                .requestTime(ldt2)
                                .tableOrBreakoutRoom("table 05")
                                .explanation("team02 broke")
                                .solved(true)
                                .build();
                ArrayList<HelpRequests> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(helpRequest1, helpRequest2));
                
                when(helpRequestsRepository.findAll()).thenReturn(expectedRequests);
                
                                // act
                MvcResult response = mockMvc.perform(get("/api/HelpRequests/all"))
                                .andExpect(status().isOk()).andReturn();
                
                                // assert
                
                verify(helpRequestsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
                        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_helprequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests helpRequest1 = HelpRequests.builder()
                        .requesterEmail("yee")
                        .teamId("yee")
                        .requestTime(ldt1)
                        .tableOrBreakoutRoom("yee")
                        .explanation("yee")
                        .solved(true)
                        .build();

                when(helpRequestsRepository.save(eq(helpRequest1))).thenReturn(helpRequest1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/HelpRequests/post?requesterEmail=yee&teamId=yee&tableOrBreakoutRoom=yee&explanation=yee&solved=true&requestTime=2022-01-03T00:00:00")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).save(helpRequest1);
                String expectedJson = mapper.writeValueAsString(helpRequest1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_helprequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests helpRequest1 = HelpRequests.builder()
                        .requesterEmail("yee")
                        .teamId("yee")
                        .requestTime(ldt1)
                        .tableOrBreakoutRoom("yee")
                        .explanation("yee")
                        .solved(false)
                        .build();

                when(helpRequestsRepository.findById(eq(15L))).thenReturn(Optional.of(helpRequest1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/HelpRequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).findById(15L);
                verify(helpRequestsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("HelpRequest with id 15 deleted", json.get("message"));
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_helprequest_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(helpRequestsRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/HelpRequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("HelpRequests with id 15 not found", json.get("message"));
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_helprequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

                HelpRequests helpRequestOrig = HelpRequests.builder()
                                .requesterEmail("jenniferzhu@gmail.com")
                                .teamId("14")
                                .requestTime(ldt1)
                                .tableOrBreakoutRoom("table 14")
                                .explanation("team01")
                                .solved(false)
                                .build();
                HelpRequests helpRequestEdited = HelpRequests.builder()
                                .requesterEmail("ehehhahaha@gmail.com")
                                .teamId("05")
                                .requestTime(ldt2)
                                .tableOrBreakoutRoom("table 05")
                                .explanation("team02 broke")
                                .solved(true)
                                .build();

                String requestBody = mapper.writeValueAsString(helpRequestEdited);

                when(helpRequestsRepository.findById(eq(67L))).thenReturn(Optional.of(helpRequestOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/HelpRequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();
                // assert
                verify(helpRequestsRepository, times(1)).findById(67L);
                verify(helpRequestsRepository, times(1)).save(helpRequestEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_helprequest_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests helpRequestEdited = HelpRequests.builder()
                                .requesterEmail("jenniferzhu@gmail.com")
                                .teamId("14")
                                .requestTime(ldt1)
                                .tableOrBreakoutRoom("table 14")
                                .explanation("team01")
                                .solved(false)
                                .build();

                String requestBody = mapper.writeValueAsString(helpRequestEdited);

                when(helpRequestsRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/HelpRequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();
                   // assert
                   verify(helpRequestsRepository, times(1)).findById(67L);
                   Map<String, Object> json = responseToJson(response);
                   assertEquals("HelpRequests with id 67 not found", json.get("message"));
   
           }

}
