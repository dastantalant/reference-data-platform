import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import Aura from '@primevue/themes/aura';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import FocusTrap from 'primevue/focustrap';
import ToastService from 'primevue/toastservice';

import '@/assets/styles.scss';
import '@/assets/tailwind.css';

import Errors from '@/components/Errors.vue';
import { http } from '@/http';
import { i18n } from '@/i18n';
import { pinia } from './stores';

const app = createApp(App);

app.component('Errors', Errors);
app.directive('focustrap', FocusTrap);

app.use(i18n);
app.use(router);
app.use(http);
app.use(pinia);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.app-dark'
    }
  }
});
app.use(ToastService);
app.use(ConfirmationService);

app.mount('#app');
