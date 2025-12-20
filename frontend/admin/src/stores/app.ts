import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    loadingCount: 0,
    loadingMessage: null,
    networkOnline: false,
  }),
  actions: {
    isLoading() {
      return this.loadingCount > 0;
    },
    beginLoading(message = null) {
      if (this.loadingCount === 0) {
        this.loadingMessage = message;
      }
      this.loadingCount++;
    },
    endLoading() {
      if (this.loadingCount > 0) {
        this.loadingCount--;
      } else {
        this.loadingMessage = null;
      }
    },
  },
});
