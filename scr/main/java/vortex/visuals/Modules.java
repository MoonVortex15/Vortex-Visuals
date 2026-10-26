package vortex.visuals;

import java.util.ArrayList;
import java.util.List;

public final class Modules {
    public static final List<Module> ALL = new ArrayList<>();

    public static final Module CLIENT_NAME = new Module("Логотип", "Отображает название клиента на экране", true);
    public static final Module FPS_COUNTER = new Module("Счетчик FPS", "Показывает текущую частоту кадров", true);
    public static final Module CPS_COUNTER = new Module("Счетчик CPS", "Показывает скорость кликов мыши", true);
    public static final Module ARMOR_STATUS = new Module("Статус брони", "Отображает прочность брони по бокам инвентаря", true);
    public static final Module KEY_BINDS = new Module("Хоткеи", "Показывает быстрые клавиши управления", true);

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
