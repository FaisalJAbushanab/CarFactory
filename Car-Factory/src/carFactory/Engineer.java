package carFactory;

public class Engineer implements Employee {

    private final int SALARY = 15;

    @Override
    public int getSalary() {
        return SALARY;
    }
}
