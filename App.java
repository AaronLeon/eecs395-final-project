import parcheesi.Parcheesi;

public class App {
    public static void main(String[] args) {
        Parcheesi app = new Parcheesi();
        try {
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
