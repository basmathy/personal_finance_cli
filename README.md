# Personal Finance CLI (Java)

CLI-приложение для управления личными финансами:
- несколько пользователей + авторизация логин/пароль
- доходы/расходы по категориям
- бюджеты по категориям
- отчёты (общие и по категориям), подсчёты по выбранным категориям и по периоду
- вывод в терминал или в файл
- оповещения (80% бюджета, превышение бюджета, расходы > доходов, отрицательный баланс)
- сохранение кошелька в JSON при выходе, загрузка при входе
- переводы между пользователями

## Запуск
Java 17+.

```bash
mvn -q package
java -jar target/personal-finance-cli-1.0.0.jar
```

## Основные команды

- `help`
- `register <login> <password>`
- `login <login> <password>`
- `logout`

- `add-category "<name>"`
- `set-budget "<category>" <amount>`

- `add-income "<category>" <amount> [note="..."]`
- `add-expense "<category>" <amount> [note="..."]`

- `transfer <toLogin> <amount> [note="..."]`

- `report [cats="c1,c2,..."] [from="2026-01-01T00:00"] [to="2026-01-15T23:59"] [out="terminal|file:path"]`

- `export-json <path>`
- `import-json <path>`
- `export-csv <path>`
- `exit`

---

## Примеры

```bash
register basmathy qwerty
login basmathy qwerty

add-category "Еда"
add-expense "Еда" 300
add-expense "Еда" 500

add-income "Зарплата" 20000

set-budget "Еда" 4000

report
report cats="Еда,Зарплата" out="file:report.txt"

exit
```
