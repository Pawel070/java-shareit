package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingQueryDto;
import ru.practicum.shareit.booking.model.Booking;

/*
@Component
public class BookingMapper {
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserServiceImpl userService, ItemServiceImpl itemService,
                         UserMapper userMapper, ItemMapper itemMapper) {
        this.userService = userService;
        this.itemService = itemService;
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto;
        if (booking != null) {
            bookingDto = new BookingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    itemMapper.toItemDto(booking.getItem()),
                    userMapper.toUserDto(booking.getBooker()),
                    booking.getStatus()
            );
        } else {
            bookingDto = null;
        }
        return bookingDto;
    }

    public BookingInfoDto toBookingInfoDto(Booking booking) {
        BookingInfoDto bookingInfoDto;
        if (booking != null) {
            bookingInfoDto = new BookingInfoDto(
                    booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd()
            );
        } else {
            bookingInfoDto = null;
        }
        return bookingInfoDto;
    }

    public Booking toBooking(BookingQueryDto bookingInputDto, Long bookerId) {
        return new Booking(
                null,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                itemService.findItemById(bookingInputDto.getItemId()),
                userService.findUserById(bookerId),
                Status.WAITING
        );
    }

 */
@Mapper//(componentModel = "spring")
public interface BookingMapper {

    //@Mapping(target = "itemId", source = "item.id")
    BookingDto toBookingDto(Booking booking);

    //@Mapping(target = "itemId", source = "item.id")
    BookingInfoDto toBookingInfoDto(Booking booking);

    //@Mapping(target = "item.id", source = "itemId")
    Booking toBooking(BookingQueryDto bookingInputDto, Long bookerId);
}
