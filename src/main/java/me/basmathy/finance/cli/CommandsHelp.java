package me.basmathy.finance.cli;

public class CommandsHelp {
    public static String text() {
        return """
                Команды:
                  help
                  register <login> <password>
                  login <login> <password>
                  logout
                  
                  add-category "<name>"
                  set-budget "<category>" <amount>
                  
                  add-income "<category>" <amount> [note="..."]
                  add-expense "<category>" <amount> [note="..."]
                  
                  transfer <toLogin> <amount> [note="..."]
                  
                  report [cats="c1,c2,..."] [from="2026-01-01T00:00"] [to="2026-01-15T23:59"] [out="terminal|file:path"]
                  
                  export-json <path>
                  import-json <path>
                  export-csv <path>
                  
                  exit
                  
                Примечания:
                  - Категории можно вводить в кавычках, если есть пробелы.
                  - amount — число > 0 (можно с точкой).
                  - report cats="Еда,Такси" покажет подсчёты только по этим категориям.
                """;
    }
}