package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class BidMapperTest {
    private final BidMapper bidMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectService projectService;

    private Project project;

    @Autowired
    public BidMapperTest( BidMapper bidMapper, UserRepository userRepository, ProjectRepository projectRepository, UserService userService, ProjectService projectService ) {
        this.bidMapper = bidMapper;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
    }

    @BeforeEach
    public void setUp() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        ProjectDto projectDto = ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        Stakeholder stakeholder = (Stakeholder) user.getType();

        this.project = projectService.createProjectFor( stakeholder, projectDto );
    }

    @AfterEach
    public void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void toEntity() {
        BidDto dto = BidDto.builder()
                .amount( 100.0 )
                .comment( "Pleeeeeeeeese" )
                .projectId( this.project.getId() )
                .build();

        assertEquals( "Pleeeeeeeeese", dto.getComment() );
        Bid bid = bidMapper.toEntity( dto );

        assertEquals( dto.getAmount(), bid.getAmount() );
        assertEquals( this.project, bid.getProject() );
    }

    @Test
    void toEntityInvalidProject() {
        BidDto dto = BidDto.builder()
                .amount( 100.0 )
                .comment( "Pleeeeeeeeese" )
                .projectId( -1 )
                .build();

        // For Some reason maven is treating ProjectNotFoundException as RunTimeException
        assertThrows( RuntimeException.class, () -> bidMapper.toEntity( dto ) );
    }

    @Test
    void toDto() {
        Bid bid = new Bid();
        bid.setId( 1L );
        bid.setProject( project );
        bid.setAmount( 20.0 );

        BidDto dto = bidMapper.toDto( bid );

        assertEquals( bid.getId(), dto.getId() );
        assertEquals( bid.getAmount(), dto.getAmount() );
        assertEquals( bid.getProject().getId(), dto.getProjectId() );
    }

    @Test
    void nullTest() {
        assertNull( bidMapper.toEntity( null ) );
    }
}
