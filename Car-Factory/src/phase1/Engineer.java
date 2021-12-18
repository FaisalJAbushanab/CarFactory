package phase1;
/**
 * Class Engineer implements Employee interface
 */
public class Engineer implements Employee {

    private final int SALARY = 18;

    @Override
    public int getSalary() {
        return SALARY;
    }
}
