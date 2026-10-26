package vortex.visuals;

import java.util.ArrayList;
import java.util.List;

public final class Modules {
    public static final List<Module> ALL = new ArrayList<>();

    public static final Module CLIENT_NAME = new Module("Название клиента", "Показывает название и версию клиента", true);
    public static final Module POTIONS_MOD = new Module("Эффекты зелий", "Показывает активные эффекты зелий", true);
    public static final Module TARGET_INFO = new Module("Информация о цели", "Показывает здоровье и экипировку цели", true);
    public static final Module KEY_BINDS = new Module("Список клавиш", "Показывает назначенные клавиши функций", true);

    public static class Module {
        public final String name;
        public final String desc;
        public boolean enabled;

        public Module(String name, String desc, boolean enabled) {
            this.name = name;
            this.desc = desc;
            this.enabled = enabled;
            ALL.add(this);
        }
    }

    private Modules() {}
}
