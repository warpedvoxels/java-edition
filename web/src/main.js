import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./styles/HomePage.css";
document.title = "Hexalite Network";

createApp(App).use(router).mount("#app");
