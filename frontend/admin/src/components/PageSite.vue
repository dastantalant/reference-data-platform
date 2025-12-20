<script setup lang="ts">
export interface Props {
  title?: string;
}

const props = defineProps<Props>();
</script>

<template>
  <div class="flex flex-col items-center justify-center">
    <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen overflow-hidden w-7/12">
      <div class="flex flex-col items-center justify-center">
        <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
          <div
            class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20"
            style="border-radius: 53px"
          >
            <div
              v-if="props.title"
              class="text-center mb-8"
            >
              <h1 class="text-900 text-3xl font-medium mb-2">
                {{ props.title }}
              </h1>
            </div>
            <slot />
          </div>
        </div>
      </div>
    </div>
    <Toast
      position="bottom-center"
      :closable="true"
    />
  </div>
</template>

<style scoped>
</style>
