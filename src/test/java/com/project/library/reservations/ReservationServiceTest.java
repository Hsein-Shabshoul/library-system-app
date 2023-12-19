package com.project.library.reservations;

import com.project.library.books.Book;
import com.project.library.books.BookRepository;
import com.project.library.exception.BadRequestException;
import com.project.library.security.config.JwtService;
import com.project.library.security.otp.GenericResponse;
import com.project.library.security.user.User;
import com.project.library.security.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)

class ReservationServiceTest {
    @Captor
    private ArgumentCaptor<Message> amqpMessage;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RabbitTemplate rabbitTemplate;
    private RabbitMessagingTemplate messagingTemplate;

    private AutoCloseable openMocks;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        messagingTemplate = new RabbitMessagingTemplate(rabbitTemplate);
        // Mock the ServletRequestAttributes
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mock the HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(attributes.getRequest()).thenReturn(request);

        // Mock the Authorization header
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");

    }

    @Test
    void testReserveBook_SuccessfulReservation() {
        // Mock user and book data
        User user = new User();
        user.setEmail("test@example.com");
        user.setPlanExpiry(LocalDateTime.now().plusDays(1));
        user.setRemainingBooks(1);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setQuantity(1);

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setEmail("test@example.com");
        reservationRequest.setBook(1L);


        // Mock repository responses
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");
        when(reservationRepository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Call the actual method
        GenericResponse response = reservationService.reserveBook(reservationRequest);

        // Assert the result
        assertNotNull(response);
        assertEquals("Reservation Successful! Book reserved: Test Book", response.getResponse());
        assertEquals(0, user.getRemainingBooks());
        assertEquals(0, book.getQuantity());
    }

    @Test
    void testReserveBook_EmailNotRegistered() {
        // Mock repository responses
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Call the actual method
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            ReservationRequest reservationRequest = new ReservationRequest();
            reservationRequest.setEmail("unknown@example.com");
            reservationService.reserveBook(reservationRequest);
        });

        // Assert the exception message
        assertEquals("Email not registered", exception.getMessage());
    }
    @Test
    void testExtractToken() {
        // Mock the ServletRequestAttributes
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mock the HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(attributes.getRequest()).thenReturn(request);

        // Mock the Authorization header
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");

        // Call the actual method
        String token = reservationService.extractToken();

        // Assert the result
        assertEquals("testToken", token);
    }

    @Test
    void testReturnBook_SuccessfulReturn() {
        // Mock user and book data
        User user = new User();
        user.setEmail("test@example.com");
        user.setPlanExpiry(LocalDateTime.now().plusDays(1));
        user.setRemainingBooks(1);
        user.setRemainingBooks(1);
        user.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setQuantity(1);

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setEmail("test@example.com");
        reservationRequest.setBook(1L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDatetime(LocalDateTime.now());
        reservation.setUser_email("test@example.com");

        // Mock the ServletRequestAttributes
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mock the HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(attributes.getRequest()).thenReturn(request);

        // Mock the Authorization header
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");

        messagingTemplate.convertAndSend("myExchange", "myQueue", "my Payload");

        // Mock repository responses
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");
        when(reservationRepository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(List.of(reservation)));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Print or log relevant information for debugging
        System.out.println("Before calling returnBook method");
        System.out.println("User: " + user);
        System.out.println("Book: " + book);
        System.out.println("Reservation: " + reservation);
        // Call the actual method
        String result = reservationService.returnBook(reservationRequest);

        // Assert the result
        verify(rabbitTemplate).send(eq("myExchange"), eq("myQueue"), amqpMessage.capture());

        assertNotNull(result);
        assertEquals("Book 'Test Book' returned.", result);
        assertEquals(2, book.getQuantity());
        assertNotNull(reservation.getReturnDatetime());
    }
    @Test
    void testReserveBook_BookAlreadyReserved() {
        User user = new User();
        user.setEmail("test@example.com");

        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mock the HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(attributes.getRequest()).thenReturn(request);

        // Mock repository responses
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setQuantity(1);
        // Mock reservation data
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReturnDatetime(null);

        // Simulate that the book is already reserved and not returned
        when(reservationRepository.findByUserIdAndBookId(anyLong(), anyLong()))
                .thenReturn(Optional.of(List.of(reservation)));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            ReservationRequest reservationRequest = new ReservationRequest();
            reservationRequest.setEmail("test@example.com");
            reservationRequest.setBook(1L);
            reservationService.reserveBook(reservationRequest);
        });

        assertEquals("Book already reserved and not returned yet.", exception.getMessage());
    }

    @Test
    void reserveBook() {
    }

    @Test
    void returnBook() {
    }
}