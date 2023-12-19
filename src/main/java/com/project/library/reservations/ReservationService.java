package com.project.library.reservations;

import com.project.library.books.Book;
import com.project.library.books.BookRepository;
import com.project.library.exception.BadRequestException;
import com.project.library.security.config.JwtService;
import com.project.library.security.otp.GenericResponse;
import com.project.library.security.user.User;
import com.project.library.security.user.UserRepository;
import com.project.library.messaging.RabbitMQConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final JwtService jwtService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public String extractToken() {
        // Get the current request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // Extract the token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extracting the token excluding "Bearer "
        }
        // Token not found in the header
        return null;
    }

    public GenericResponse reserveBook(ReservationRequest reservationRequest){
        Optional<User> userOptional = userRepository.findByEmail(reservationRequest.getEmail());
        if (userOptional.isEmpty()){
            throw new BadRequestException("Email not registered");
        }
        if(!userOptional.get().getEmail().equals(jwtService.extractUsername(extractToken()))){
            throw new BadRequestException("Provided email does not match signed in email.");
        }

        Optional<List<Reservation>> reservedBookOptional = reservationRepository.findByUserIdAndBookId(userOptional.get().getId(),reservationRequest.getBook());
        if (reservedBookOptional.isPresent()) {
            //reservation list of the specific book by the user
            List<Reservation> reservedBooks = reservedBookOptional.get();
            //getting reservations where return date is null(not returned yet)
            List<Reservation> reservationsWithNullReturnDatetime = reservedBooks
                    .stream()
                    .filter(reservation -> reservation.getReturnDatetime() == null)
                    .toList();
            //if there exist a reservation for this book by this user not returned, don't allow reservation
            if (!reservationsWithNullReturnDatetime.isEmpty()){
                throw new BadRequestException("Book already reserved and not returned yet.");
            }
        }

        Optional<Book> bookOptional = bookRepository.findById(reservationRequest.getBook());
        if (bookOptional.isEmpty()){
            throw new BadRequestException("Book not found");
        }
        else if (bookOptional.get().getQuantity() <= 0){
            throw new BadRequestException("Book out of stock");
        }
        LocalDateTime now = LocalDateTime.now();
        User user = userOptional.get();
        //if user sub not expired, reserve book and reduce remaining books
        if (user.getPlanExpiry().isAfter(now)) {
            Integer remainingBooks = user.getRemainingBooks();
            if (remainingBooks > 0) {
                Book book = bookOptional.get();
                int newQuantity = book.getQuantity() - 1;
                book.setQuantity(newQuantity);
                user.setRemainingBooks(remainingBooks - 1);
                LocalDateTime currentDatetime = LocalDateTime.now();
                Reservation reservation = new Reservation();
                reservation.setUser(userOptional.get());
                reservation.setBook(bookOptional.get());
                reservation.setUser_email(userOptional.get().getEmail());
                reservation.setReservationDatetime(currentDatetime);
                reservationRepository.save(reservation);
                userRepository.save(user);
                bookRepository.save(book);
                return GenericResponse.builder().response("Reservation Successful! Book reserved: " + book.getTitle()).build();
            }
            else {
                return GenericResponse.builder().response("Max allowed of reservations reached, subscribe to a bigger plan to get more.").build();
            }
        }
        else {
            return GenericResponse.builder().response("Subscription Expired, please renew.").build();
        }

    }

    public String returnBook(ReservationRequest reservationRequest){
        Optional<User> userOptional = userRepository.findByEmail(reservationRequest.getEmail());
        if (userOptional.isEmpty()){
            throw new BadRequestException("Email not registered");
        }
        if(!userOptional.get().getEmail().equals(jwtService.extractUsername(extractToken()))){
            throw new BadRequestException("Provided email does not match signed in email.");
        }
        Long userId = userOptional.get().getId();
        Long bookId = reservationRequest.getBook();
        Optional<List<Reservation>> reservedBookOptional = reservationRepository.findByUserIdAndBookId(userId,bookId);

        if(reservedBookOptional.isPresent()){
            List<Reservation> reservedBooks = reservedBookOptional.get();

            Optional<Book> bookOptional = bookRepository.findById(reservationRequest.getBook());
            if (bookOptional.isEmpty()){
                throw new BadRequestException("Book not found");
            }
            Book book = bookOptional.get();
            int newQuantity = book.getQuantity() + 1;
            book.setQuantity(newQuantity);

            List<Reservation> reservationsWithNullReturnDatetime = reservedBooks
                    .stream()
                    .filter(reservation -> reservation.getReturnDatetime() == null)
                    .toList();

            LocalDateTime currentDatetime = LocalDateTime.now();

            if(reservationsWithNullReturnDatetime.isEmpty()){
                throw new BadRequestException("Book already returned");
            }
            else {
                reservationsWithNullReturnDatetime.get(0).setReturnDatetime(currentDatetime);
                bookRepository.save(book);
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_AVERAGE_DIRECT,RabbitMQConfig.ROUTING_KEY_AVERAGE_FIND,reservationsWithNullReturnDatetime.get(0).getId());
                return "Book '" + book.getTitle() + "' returned.";
            }
        }
        else {
            throw new BadRequestException("Reservation not found");
        }
    }
}
//        Optional<List<Reservation>> reservationsOptional = reservationRepository.findByUserId(userOptional.get().getId());
//        if(reservationsOptional.isPresent()){
//            //list of all reservation objects for this user
//            List<Reservation> reserved = reservationsOptional.get();
//            //list of book ids reserved by the user
//            List<Long> allReservedBooks = reserved.stream()
//                .map(Reservation::getBook)
//                .map(Book::getId)
//                .toList();
//            if(allReservedBooks.contains(reservationRequest.getBook())){
//                Optional<List<Reservation>> reservedBooks = reservationRepository.findByUserIdAndBookId(userOptional.get().getId(),reservationRequest.getBook());
//                //if(reservedBooks.get().getReturnDatetime().equals(null))
//                    throw new BadRequestException("Book already reserved and not returned yet");
//            }
//        }