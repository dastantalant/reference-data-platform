<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

export interface Props {
  items: any[]
}

const { t } = useI18n();
const props = defineProps<Props>();

const errors = computed(() => props.items.map((item: any) => {
  let message: string;
  const field = t(`attributes.${item.$property}`);

  if (item.$validator === '$externalResults') {
    message =  t(`validation.rules.${item.$message}`, { field });
  } else {
    const key = item.$validator || item.$params?.type;
    const params = { field, ...item.$params };
    message = t(`validation.rules.${key}`, params);
  }

  return {
    uid: item.$uid,
    message,
  };
}));
</script>

<template>
  <div
    v-if="errors.length"
    class="field-errors"
  >
    <small
      v-for="error in errors"
      class="p-error"
      :key="error.uid"
    >
      {{ error.message }}
    </small>
  </div>
</template>

<style lang="scss">
.field-errors {
  margin-bottom: 1rem;
}
</style>
