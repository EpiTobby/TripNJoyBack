package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.model.ExpenseModel;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.request.ExpenseRequest;
import fr.tobby.tripnjoyback.model.request.MoneyDueRequest;
import fr.tobby.tripnjoyback.model.response.BalanceResponse;
import fr.tobby.tripnjoyback.model.response.MoneyDueResponse;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DataJpaTest
public class ExpenseServiceTest {
    private static GenderEntity maleGender;
    private static GenderRepository genderRepository;
    private static CityRepository cityRepository;
    private static LanguageRepository languageRepository;
    private static StateRepository stateRepository;
    private static CityEntity cityEntity;
    private static LanguageEntity languageEntity;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpenseMemberRepository expenseMemberRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    private ExpenseService expenseService;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository,
                          @Autowired ApplicationContext context,
                          @Autowired CityRepository cityRepository, @Autowired LanguageRepository languageRepository)
    {
        maleGender = genderRepository.save(new GenderEntity("male"));
        ExpenseServiceTest.genderRepository = genderRepository;
        ExpenseServiceTest.cityRepository = cityRepository;
        ExpenseServiceTest.languageRepository = languageRepository;

        stateRepository.save(new StateEntity("CLOSED"));
        stateRepository.save(new StateEntity("OPEN"));
        cityEntity = cityRepository.save(new CityEntity("Paris"));
        languageEntity = languageRepository.save(new LanguageEntity("French"));
        ExpenseServiceTest.stateRepository = stateRepository;
        SpringContext.setContext(context);
    }

    @BeforeEach
    void setUp()
    {
        expenseService = new ExpenseService(expenseMemberRepository, expenseRepository, userRepository, groupRepository);
    }

    @AfterEach
    void tearDown()
    {
        expenseRepository.deleteAll();
        expenseMemberRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll()
    {
        genderRepository.deleteAll();
        stateRepository.deleteAll();
        cityRepository.deleteAll();
        languageRepository.deleteAll();
    }

    private GroupEntity anyGroup()
    {
        GroupEntity group = new GroupEntity(null, "test","description", State.OPEN.getEntity(), null, 10, new Date(), null, null,
                "","",new ArrayList<>(), null, new ArrayList<>());
        return groupRepository.save(group);
    }

    @NotNull
    private UserEntity anyUser() throws ParseException
    {
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email("test@1.com")
                .birthDate(new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000").toInstant())
                .city(cityEntity)
                .confirmed(true)
                .language(languageEntity)
                .roles(List.of())
                .build());
    }

    @Test
    void createExpenseEvenlyDivided() throws ParseException{
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser();
        UserEntity user2 = anyUser();
        UserEntity user3 = anyUser();
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user1, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user2, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user3, null, false));

        ExpenseRequest expenseRequest = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(true)
                .total(60)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).build()))
                .build();

        ExpenseModel expenseModel = expenseService.createExpense(groupEntity.getId(),user1.getId(), expenseRequest);
        Assertions.assertFalse(expenseRepository.findById(expenseModel.getId()).isEmpty());
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupEntity.getId());
        Assertions.assertEquals(3, expenseMemberEntities.size());
        Assertions.assertEquals(20, expenseMemberEntities.get(0).getAmountToPay());
        Assertions.assertEquals(60, expenseMemberEntities.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum());
    }

    @Test
    void createExpenseNotEvenlyDivided() throws ParseException{
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser();
        UserEntity user2 = anyUser();
        UserEntity user3 = anyUser();
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user1, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user2, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user3, null, false));

        ExpenseRequest expenseRequest = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(60)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(10d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(20d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(30d).build()))
                .build();

        ExpenseModel expenseModel = expenseService.createExpense(groupEntity.getId(),user1.getId(), expenseRequest);
        Assertions.assertFalse(expenseRepository.findById(expenseModel.getId()).isEmpty());
        List<ExpenseMemberEntity> expenseMemberEntities = expenseMemberRepository.findByGroupId(groupEntity.getId());
        Assertions.assertEquals(3, expenseMemberEntities.size());
        Assertions.assertEquals(60, expenseMemberEntities.stream().mapToDouble(ExpenseMemberEntity::getAmountToPay).sum());
    }

    @Test
    void MoneyUserOwes()  throws ParseException{
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser();
        UserEntity user2 = anyUser();
        UserEntity user3 = anyUser();
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user1, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user2, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user3, null, false));

        ExpenseRequest expenseRequest1 = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(60)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(10d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(20d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(30d).build()))
                .build();

        ExpenseRequest expenseRequest2 = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(30)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(6d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(12d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(12d).build()))
                .build();
        expenseService.createExpense(groupEntity.getId(),user3.getId(), expenseRequest1);
        expenseService.createExpense(groupEntity.getId(),user3.getId(), expenseRequest2);

        List<MoneyDueResponse> moneyDueResponses = expenseService.getMoneyUserOwesToEachMemberInGroup(groupEntity.getId(), user1.getId());
        Assertions.assertEquals(moneyDueResponses.get(0).getTotal(),16);
        moneyDueResponses = expenseService.getMoneyUserOwesToEachMemberInGroup(groupEntity.getId(), user2.getId());
        Assertions.assertEquals(moneyDueResponses.get(0).getTotal(),32);
    }

    @Test
    void MembersOwesToUser()  throws ParseException{
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser();
        UserEntity user2 = anyUser();
        UserEntity user3 = anyUser();
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user1, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user2, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user3, null, false));

        ExpenseRequest expenseRequest1 = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(60)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(10d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(20d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(30d).build()))
                .build();

        ExpenseRequest expenseRequest2 = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(30)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(6d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(12d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(12d).build()))
                .build();
        expenseService.createExpense(groupEntity.getId(),user3.getId(), expenseRequest1);
        expenseService.createExpense(groupEntity.getId(),user3.getId(), expenseRequest2);

        List<MoneyDueResponse> moneyDueResponses = expenseService.getMoneyEachMemberOwesToUserInGroup(groupEntity.getId(), user3.getId());
        Assertions.assertEquals(moneyDueResponses.get(0).getTotal(),16);
        Assertions.assertEquals(moneyDueResponses.get(1).getTotal(),32);
    }

    @Test
    void balance()  throws ParseException{
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser();
        UserEntity user2 = anyUser();
        UserEntity user3 = anyUser();
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user1, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user2, null, false));
        groupEntity.getMembers().add(new GroupMemberEntity(groupEntity, user3, null, false));

        ExpenseRequest expenseRequest1 = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(60)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(10d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(20d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(30d).build()))
                .build();

        ExpenseRequest expenseRequest2 = ExpenseRequest.builder()
                .description("Food")
                .isEvenlyDivided(false)
                .total(30)
                .moneyDueByEachUser(List.of(MoneyDueRequest.builder().userId(user1.getId()).money(6d).build(),
                        MoneyDueRequest.builder().userId(user2.getId()).money(12d).build(),
                        MoneyDueRequest.builder().userId(user3.getId()).money(12d).build()))
                .build();
        expenseService.createExpense(groupEntity.getId(),user3.getId(), expenseRequest1);
        expenseService.createExpense(groupEntity.getId(),user3.getId(), expenseRequest2);

        List<BalanceResponse> balanceResponses = expenseService.computeBalances(groupEntity.getId());

        Assertions.assertEquals(balanceResponses.get(2).getMoney(),48);
        Assertions.assertEquals(balanceResponses.stream().mapToDouble(BalanceResponse::getMoney).sum(),0);
    }
}
