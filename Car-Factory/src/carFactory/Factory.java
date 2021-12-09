package carFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class Factory extends Building {

    private ArrayList<Warehouse> allwarehouses = new ArrayList<>();
    private ArrayList<Warehouse> warehouseAccess = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();
    private int[] employeesList = new int[3];
    private int[] warehouseAccessTotalMaterials = new int[9];
    private int operatingCost;
    private boolean isOccupied = false;

    public Factory(int[] workerCapacity, String location, int workingHours) {
        super(workerCapacity, location, workingHours);

        setEmployees();
        setEmployeesList();
        setOperatingCost();
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied() {
        isOccupied = true;
    }
    public void setUnOccupied() {
        isOccupied = false;
    }

    private void setOperatingCost() {
        for (Employee employee : employees) {
//            System.out.println(employee.toString());
            operatingCost += employee.getSalary();
        }
        System.out.println(operatingCost);
    }

    private void setEmployeesList() {
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                employeesList[0] += 1;
            } else if (employee instanceof Technician) {
                employeesList[1] += 1;
            } else {
                employeesList[2] += 1;;
            }
        }
    }

     private void setEmployees() {
        // TODO salary on location
        int max = super.getCapacity()[0] + super.getCapacity()[1] + super.getCapacity()[2];
        int min = max/2;
        int numOfEmployees = (int)Math.floor(Math.random()*((max)-min+1)+min);
        int worker = 0;
        int technician = 0;
        int engineer = 0;
        for(int i=0; i < numOfEmployees; i++) {
            int rand = (int)Math.floor(Math.random()*(6)+1);
            if((3 >= rand) && (worker < super.getCapacity()[0])) {
                Employee employee = new Worker();
                employees.add(employee);
                worker++;
            }else if((5 >= rand) && (technician < super.getCapacity()[1])) {
                Employee employee = new Technician();
                employees.add(employee);
                technician++;
            }else if((5 < rand) && (engineer < super.getCapacity()[2])){
                Employee employee = new Engineer();
                employees.add(employee);
                engineer++;
            }
            else {
                continue;
            }
        }
    }

    public void setWarehouseAccess(ArrayList<Warehouse> warehouses) {
        allwarehouses = warehouses;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getLocation().equals(super.getLocation())) {
                warehouseAccess.add(warehouse);
            }
        }
        setWarehouseTotalMaterial();
    }

    public int[] getRequirments(Request request, int index) {
        int calculatedCost = -1;
        int calculatedTime = -1;
        System.out.printf("Factory#" + index + " of request#(%s/%s:%s)\n",
                request.getDay(), request.getHour(), request.getMinute());
        if(checkMaterial(request.getComputers()) & checkTime(request.getComputers())) {
            calculatedCost = calculateCostMats(getComputersTotalMaterial(request.getComputers())) + operatingCost;
            calculatedTime = calculateTime(request.getComputers());
            System.out.printf("cost is: %d\ntime is: %d\n", calculatedCost, calculatedTime );
        }
        return new int[] {calculatedCost, calculatedTime};
    }

    private void setWarehouseTotalMaterial() {
        for (Warehouse access : warehouseAccess) {
            int[] warehouseMaterial = access.getMaterialQuantity();
            for (int i = 0; i < warehouseAccessTotalMaterials.length; i++) {
                warehouseAccessTotalMaterials[i] += warehouseMaterial[i];
            }
        }
    }

    public int[] getWarehouseTotalMaterials() {
        return warehouseAccessTotalMaterials;
    }

    public int[] getComputersTotalMaterial(ArrayList<Computer> computers) {
        int[] computerSumMaterials = new int[9];
        for (Computer comps : computers) {
            int[] compsMaterial = comps.getConstructMaterial();
            for (int i = 0; i < compsMaterial.length; i++) {
                computerSumMaterials[i] += compsMaterial[i];
            }
        }
        return computerSumMaterials;
    }

    public int calculateCostMats(int[] computerSumMaterials) {
        double matsCost = 1000000000;
        double tempCost = 0;
        for (Warehouse access : warehouseAccess) {
            double[] materialPrice = access.getMaterial().getMaterialPrice();
            for (int i = 0; i < materialPrice.length; i++) {
                tempCost += materialPrice[i] * computerSumMaterials[i];
            }
//            System.out.println("temp cost is" + tempCost);
//            System.out.println("mats cost is" + matsCost);
            if (tempCost < matsCost) {
                matsCost = tempCost;
            }
            tempCost = 0;
        }
        return (int) matsCost;
    }

    private boolean checkMaterial(ArrayList<Computer> computers) {
        boolean materialSufficient = true;
        for (int i = 0; i < getWarehouseTotalMaterials().length; i++) {
            if (getComputersTotalMaterial(computers)[i] > getWarehouseTotalMaterials()[i]) {
                materialSufficient = false;
                break;
            }
        }
        System.out.println("material sufficient: " + materialSufficient);
        return materialSufficient;
    }

    private boolean checkTime(ArrayList<Computer> computers) {
        boolean timeSufficient = true;
        int[] computerSumEmployee = new int[3];
        for (Computer comps : computers) {
            for (int i = 0; i < computerSumEmployee.length; i++) {
                computerSumEmployee[i] += comps.getNumberOfEmployees()[i];
            }
        }
        for (int i = 0; i < computerSumEmployee.length; i++) {
            if ((computerSumEmployee[i] > super.getCapacity()[i])) {
                timeSufficient = false;
                break;
            }
//        System.out.println(computerSumEmployee[0] + " and " + super.buildingCapacity[0]);
//        System.out.println(computerSumEmployee[1] + " and " + super.buildingCapacity[1]);
//        System.out.println(computerSumEmployee[2] + " and " + super.buildingCapacity[2]);
//        boolean timeSufficient = !((computerSumEmployee[0] > super.getCapacity()[0])
//                                || (computerSumEmployee[1] > super.getCapacity()[1])
//                                || (computerSumEmployee[2] > super.getCapacity()[2]));
        }
        System.out.println("time sufficient: " + timeSufficient);
        return timeSufficient;
    }

    private int calculateTime(ArrayList<Computer> computers) {
        int worker = 0;
        int technnician = 0;
        int engineer = 0;
//        System.out.println("in calc time");
        double time = 0;
//        System.out.println("time start is:" + time);
        double repeated = 0;
        boolean breakedOut = false;
        while((worker < employeesList[0]) &&
                (technnician < employeesList[0]) &&
                (engineer < employeesList[0])) {
            for (Computer computer: computers) {
                worker += computer.getNumberOfEmployees()[0];
                technnician += computer.getNumberOfEmployees()[1];
                engineer += computer.getNumberOfEmployees()[2];
                if((worker < employeesList[0]) &&
                    (technnician < employeesList[0]) &&
                    (engineer < employeesList[0])) {
                    time += 1/(repeated+1);
//                    System.out.println("time is:" + time);
                } else {
                    breakedOut = true;
                    break;
                }
            }
            repeated++;
            if (!breakedOut) {
                time -= computers.size()/(repeated+1);
            }
        }
        return (int) time;
    }

    public int getOperatingCost() {
        return operatingCost;
    }

    public String getFactoryInformation() {
        String info = "Location: " + super.getLocation() +
                " | Working hours: " + super.getWorkingHours() +
                " | Operating cost: " + getOperatingCost() +"\n";
        info += "Employees capacity: " + + super.getCapacity()[0] + " workers "
                + super.getCapacity()[1] + " technicians "
                + super.getCapacity()[2] + " engineers \n";
        info += "Employees: " + employeesList[0] + " workers "
                + employeesList[1] + " technicians "
                + employeesList[2] + " engineers \n";
        info += "Access to warehouses: ";
        for (Warehouse access: warehouseAccess) {
            info += "#" + (allwarehouses.indexOf(access)+1) + " ";
        }
        info += "\nMaterials: [aluminium grams, plastic grams, glass grams, silicon mg " +
                ", gold mg, copper mg, iron grams, chrome mg, silver mg]\n" +
                "Amount: " + Arrays.toString(getWarehouseTotalMaterials()) + "\n";
        return info + "\n";
    }
}

