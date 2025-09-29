package org.matvey.bankrest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.TransferRequestDto;
import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.entity.CardStatus;
import org.matvey.bankrest.entity.Role;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.repository.CardRepository;
import org.matvey.bankrest.repository.RoleRepository;
import org.matvey.bankrest.repository.UserRepository;
import org.matvey.bankrest.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Create roles
        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        // Create test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("user@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRoles(Set.of(userRole));
        userRepository.save(testUser);

        // Create admin user
        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        // Generate tokens
        userToken = jwtUtil.generateToken(testUser.getEmail());
        adminToken = jwtUtil.generateToken(adminUser.getEmail());
    }

    @Test
    void createCard_WhenValidRequest_ShouldCreateCard() throws Exception {
        // Given
        CardRequestDto cardRequest = new CardRequestDto();
        cardRequest.setExpirationDate(LocalDate.now().plusYears(3));
        cardRequest.setBalance(BigDecimal.valueOf(1000));

        // When & Then
        mockMvc.perform(post("/cards")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.owner.email").value("user@example.com"));
    }

    @Test
    void createCard_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        CardRequestDto cardRequest = new CardRequestDto();
        cardRequest.setExpirationDate(LocalDate.now().minusYears(1)); // Past date
        cardRequest.setBalance(BigDecimal.valueOf(-100)); // Negative balance

        // When & Then
        mockMvc.perform(post("/cards")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMyCards_WhenUserHasCards_ShouldReturnCards() throws Exception {
        // Given
        Card card = new Card();
        card.setCardNumber("encrypted_number");
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(500));
        card.setOwner(testUser);
        cardRepository.save(card);

        // When & Then
        mockMvc.perform(get("/cards/my")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].balance").value(500))
                .andExpect(jsonPath("$.content[0].cardStatus").value("ACTIVE"));
    }

    @Test
    void transferBetweenCards_WhenValidRequest_ShouldTransferFunds() throws Exception {
        // Given
        Card fromCard = new Card();
        fromCard.setCardNumber("encrypted_from");
        fromCard.setExpirationDate(LocalDate.now().plusYears(3));
        fromCard.setCardStatus(CardStatus.ACTIVE);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setOwner(testUser);
        cardRepository.save(fromCard);

        Card toCard = new Card();
        toCard.setCardNumber("encrypted_to");
        toCard.setExpirationDate(LocalDate.now().plusYears(3));
        toCard.setCardStatus(CardStatus.ACTIVE);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setOwner(testUser);
        cardRepository.save(toCard);

        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(fromCard.getId());
        transferRequest.setToCardId(toCard.getId());
        transferRequest.setAmount(BigDecimal.valueOf(200));
        transferRequest.setDescription("Test transfer");

        // When & Then
        mockMvc.perform(post("/cards/transfer")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void blockCard_WhenValidRequest_ShouldBlockCard() throws Exception {
        // Given
        Card card = new Card();
        card.setCardNumber("encrypted_number");
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(500));
        card.setOwner(testUser);
        cardRepository.save(card);

        // When & Then
        mockMvc.perform(post("/cards/{id}/block", card.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardStatus").value("BLOCKED"));
    }

    @Test
    void getAllCards_WhenAdmin_ShouldReturnAllCards() throws Exception {
        // Given
        Card card = new Card();
        card.setCardNumber("encrypted_number");
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(500));
        card.setOwner(testUser);
        cardRepository.save(card);

        // When & Then
        mockMvc.perform(get("/cards")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllCards_WhenUser_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/cards")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCard_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        // Given
        CardRequestDto cardRequest = new CardRequestDto();
        cardRequest.setExpirationDate(LocalDate.now().plusYears(3));

        // When & Then
        mockMvc.perform(post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isUnauthorized());
    }
}