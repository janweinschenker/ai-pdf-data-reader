# Unit Testing Guidelines

This document outlines best practices and conventions for writing unit tests in this project.

## 1. Structure with `// given`, `// when`, `// then`

Tests should be structured with clear `// given`, `// when`, and `// then` comment blocks to separate test phases.

**Example:**

```java

@Test
void shouldReturnDiscountedPrice() {
    // given
    final Product product = new Product("Book", 20.0);
    final DiscountService discountService = new DiscountService();

    // when
    final double discountedPrice = discountService.applyDiscount(product, 0.10);

    // then
    assertEquals(18.0, discountedPrice);
}
```

```java

@Test
void updateFleetSetting_no_id() {
    // given
    final FleetSettingDto dto = new FleetSettingDto();
    dto.setId(null);
    final FleetSetting domain = FleetSettingMapper.INSTANCE.toDomainObject(dto);

    // when
    final var result = sut.updateFleetSetting(domain);

    // then
    StepVerifier.create(result)
            .expectErrorMatches(err -> isMatchingExceptionType(err, IllegalArgumentException.class))
            .verify();
    verify(writeSettingService, never()).saveFleetSetting(any());
}

```

## 2. Use JUnit 5 Features

- Use `@DisplayName` for human-readable test descriptions
- Avoid legacy JUnit 4 features unless required

## 3. Mocks and Fakes

- Prefer real implementations where possible
- Use Mockito with clear purpose
- Inject mocks via @InjectMocks if possible
- Use `@Mock` for dependencies that are not under test
- Use `@Spy` for partial mocks when necessary
- Name the class under test as `sut` (System Under Test)
- When Date or Time is involved, use `java.time` classes (e.g., `LocalDate`, `LocalDateTime`) instead of
  `java.util.Date`.
- When Date or Time is involved, do not use `LocalDate.now()` or `LocalDateTime.now()` directly in tests. Instead, use a
  fixed date/time or a clock abstraction to control the time in tests.
- Use `@ExtendWith(MockitoExtension.class)` to enable Mockito annotations
- When a variable `partnerId` is used in tests, ensure it is set to a valid value, e.g `TestData.WZZ`.

## 4. Assertions

- Use `assertEquals`, `assertTrue`, `assertFalse`, etc. for clarity
- Use `assertThrows` for exception testing

## 5. Test Naming

- Use descriptive names that reflect the behavior being tested
- Use the format `[NameOfTestedMethod]: should[ExpectedBehavior]When[Condition]`

## 6. Test Coverage

- Aim for high coverage but prioritize meaningful tests over quantity
- Focus on edge cases and error conditions

## 7. Test Isolation

- Each test should be independent
- Avoid shared state between tests

## 8. Avoid External Dependencies

- Do not rely on external systems (databases, APIs) in unit tests
- Use mocks or fakes for external interactions

## 9. Coding Standards
- Follow the project's [coding standards and conventions](STYLE_GUIDE.md) from 
