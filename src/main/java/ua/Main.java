package ua;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    static EntityManagerFactory emf;
    static EntityManager em;
    static Menu menu;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        initTable();
        while (true) {
            System.out.println("1: add dish");
            System.out.println("2: select dishes where price are from... to...");
            System.out.println("3: select dishes with discount");
            System.out.println("4: select dishes <= 1kg");
            System.out.println("5: to exit");
            System.out.println("7: show dishes");
            System.out.print("-> ");
            String s = sc.nextLine();
            switch (s) {
                case "1":
                    addDish(sc);
                    break;
                case "2":
                    selectDishFromTo(sc);
                    break;
                case "3":
                    selectDishDiscount();
                    break;
                case "4":
                    selectDishKg(sc);
                    break;
                case "5":
                    return;
                case "7":
                    showDishes(sc);
                    break;
                default:
                    break;
            }
        }
    }

    private static void initTable() {
        emf = Persistence.createEntityManagerFactory("JPAMenu");
        em = emf.createEntityManager();
        Scanner sc = new Scanner(System.in);
        menu = new Menu("main");
        Dish dish1 = new Dish("fish", 280, 280, 10);
        Dish dish2 = new Dish("borsch", 90, 250, 10);
        Dish dish3 = new Dish("potato-free", 35, 150, 10);
        Dish dish4 = new Dish("vareniki", 50, 200, 0);
        Dish dish5 = new Dish("desert", 280, 100, 0);
        Dish dish6 = new Dish("kalmar", 350, 350, 10);
        menu.addDish(dish1);
        menu.addDish(dish2);
        menu.addDish(dish3);
        menu.addDish(dish4);
        menu.addDish(dish5);
        menu.addDish(dish6);
        em.getTransaction().begin();
        try {
            em.persist(menu);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void addDish(Scanner sc) {
        System.out.println("Enter dish name");
        String name = sc.nextLine();
        System.out.println("Enter dish price");
        String sPrice = sc.nextLine();
        int price = Integer.parseInt(sPrice);
        System.out.println("Enter dish weight");
        String sWeight = sc.nextLine();
        int weight = Integer.parseInt(sWeight);
        System.out.println("Enter dish discount from 0 to 50 : ");
        String sDiscount = sc.nextLine();
        int discount = Integer.parseInt(sDiscount);
        Dish dish = new Dish(name, price, weight, discount);
        em.getTransaction().begin();
        try {
            menu.addDish(dish);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void selectDishFromTo(Scanner sc) {
        System.out.println("Enter from: ");
        String sFrom = sc.nextLine();
        int from = Integer.parseInt(sFrom);
        System.out.println("Enter to: ");
        String sTo = sc.nextLine();
        int to = Integer.parseInt(sTo);
        try {
            Query query = em.createNamedQuery("fromTo", Dish.class);
            query.setParameter("from", from);
            query.setParameter("to", to);
            List<Dish> dishList = query.getResultList();
            for (Dish d : dishList) {
                System.out.println(d);
            }
        } catch (NoResultException ex) {
            System.out.println("no result!");
        } catch (NonUniqueResultException ex) {
            System.out.println("non unique result!");
        }
    }

    private static void selectDishDiscount() {
        try {
            Query query = em.createNamedQuery("discount", Dish.class);
            List<Dish> listDiscount = query.getResultList();
            for (Dish d : listDiscount) {
                System.out.println(d);
            }

        } catch (NoResultException ex) {
            System.out.println("no result found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("non unique result!");
            return;
        }
    }

    private static void selectDishKg(Scanner sc) {
        System.out.println("Enter name of set dishes:");
        String name = sc.nextLine();
        Menu menu1 = new Menu(name);
        while (!menu1.isFlag()) {
            Dish dish = selectDish(sc);
            if (dish != null) {
                if ((menu1.getTotalWeight() + dish.getWeight()) <= 1000) {
                    menu1.addDish(dish);
                    menu1.setTotalWeight(menu1.getTotalWeight() + dish.getWeight());
                } else {
                    menu1.setFlag(true);
                }
            }
        }
        em.getTransaction().begin();
        try {
            em.persist(menu1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static Dish selectDish(Scanner sc) {
        Query query = em.createNamedQuery("selectDish", Dish.class);
        System.out.println("Select dish id: ");
        String sId = sc.nextLine();
        long id = Long.parseLong(sId);
        query.setParameter("id", id);
        Dish dish = em.find(Dish.class, id);
        if (dish == null) {
            System.out.println("The dish with this id not found!");
            showMainDishes(sc);
            selectDish(sc);
        }
        return dish;
    }

    private static void showDishes(Scanner sc) {
        System.out.println("Enter menu name (main) or name of set dishes < 1kg : ");
        String name = sc.nextLine();
        try {
            Query query = em.createNamedQuery("Menu.findByName", Menu.class);
            query.setParameter("name", name);
            Menu menu = (Menu) query.getSingleResult();
            for (Dish dish : menu.getDishes()) {
                System.out.println(dish);
            }
            System.out.println("-----------------------------------------------");
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        } catch (NoResultException ex) {
            System.out.println("No result!");
            return;
        }
    }

    private static void showMainDishes(Scanner sc) {
        for (Dish dish : menu.getDishes()) {
            System.out.println(dish);
        }
    }
}
