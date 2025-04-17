# Contributing to ConstraintCrafter

Thank you for considering contributing to **ConstraintCrafter**! We welcome improvements, bug fixes, and new features.

## Getting Started

1. Fork the repository on GitHub: `https://github.com/ryvin/ConstraintCrafter`
2. Clone your fork:
   ```bash
   git clone https://github.com/ryvin/ConstraintCrafter.git
   ```
3. Create a feature branch:
   ```bash
   git checkout -b feature/awesome-feature
   ```
4. Install dependencies and build:
   ```bash
   mvn clean package
   ```
5. Write your code and tests in `src/main/java` and `src/test/java`.
6. Run tests locally:
   ```bash
   mvn test
   ```
7. Commit your changes:
   - Use descriptive commit messages (e.g. `feat: add maxOccurs validation`).
   - Follow existing code conventions.
8. Push to your fork:
   ```bash
   git push origin feature/awesome-feature
   ```
9. Open a Pull Request against `main` branch on the upstream repo. Describe your changes and link any related issues.

## Code Style & Guidelines

- Follow the existing Java coding style (Java 11 conventions).
- Ensure all new code has unit tests and passes existing tests.
- Keep methods short and focused; avoid duplication.
- Update documentation (`README.md`, Javadocs) as needed.

## Reporting Issues

- Use GitHub Issues to report bugs or request enhancements.
- Provide a clear description, steps to reproduce, and expected vs. actual behavior.

## Support

For questions or feedback, reach out on GitHub: [@ryvin](https://github.com/ryvin).
