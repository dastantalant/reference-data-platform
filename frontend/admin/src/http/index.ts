import { PLUGIN_HTTP } from '@/global';
import axios, { AxiosInstance } from 'axios';
import { App, inject, Plugin } from 'vue';

/*
const GUEST_ROUTES = [
  '/login',
  '/register',
  '/verify-email',
  '/send-verify-email-link',
  '/send-reset-link',
  '/reset-password',
];
*/

function createHttp(): AxiosInstance {
  const http = axios.create({
    timeout: 28000,
    baseURL: import.meta.env.VITE_APP_URL,
  });

  http.interceptors.request.use(
    (config) => {
      /*
      const isGuest = GUEST_ROUTES.some((route) => config.url.endsWith(route));
      const userStore = useUserStore();

      if (userStore.isAuthenticated && !isGuest) {
        const { tenant, data } = userStore;
        // eslint-disable-next-line dot-notation
        config.headers['Authorization'] = `Bearer ${data.api_token}`;

        if (tenant) {
          config.headers['X-Company'] = tenant;
        }
      }
      */
      return config;
    },
    (error) => Promise.reject(error),
  );

  return http;
}

export function useHttp(): AxiosInstance {
  return inject(PLUGIN_HTTP);
}

export const http: Plugin = {
  install(app: App) {
    app.provide(PLUGIN_HTTP, createHttp());
  }
};
