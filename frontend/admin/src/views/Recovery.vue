<script setup lang="ts">
import PageSite from '@/components/PageSite.vue';
import { decodeErrors, TOAST_LIFE } from '@/global';
import { useAuth } from '@/services/AuthService';
import { useAppStore } from '@/stores/app';
import useVuelidate from '@vuelidate/core';
import { email, required } from '@vuelidate/validators';
import { useToast } from 'primevue/usetoast';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const { sendResetLink } = useAuth();
const state = ref<any>({});
const appStore = useAppStore();
const toast = useToast();

const $externalResults = ref<any>({});

const rules = computed(() => ({
  username: { required, email },
}));

const validator = useVuelidate(rules, state, {
  $lazy: true,
  $rewardEarly: true,
  $autoDirty: true,
  $externalResults,
});

const onSubmit = async () => {
  appStore.beginLoading();
  try {
    validator.value.$clearExternalResults();
    const isValid = await validator.value.$validate();

    if (isValid) {
      const error = await sendResetLink(state.value.username);

      if (error) {
        $externalResults.value = decodeErrors([{
          field: 'username',
          rule: error
        }]);
      } else {
        toast.add({
          severity: 'success',
          summary: t('success'),
          detail: t('message.recoveryEmailSuccess'),
          life: TOAST_LIFE,
          closable: false
        })
      }

    }
  } finally {
    appStore.endLoading();
  }
};
</script>

<template>
  <PageSite
    :title="t('passwordRecovery')"
    v-focustrap
  >
    <form @submit.prevent="onSubmit">
      <label
        for="username"
        class="block text-900 text-xl font-medium mb-2"
      >
        {{ t('attributes.username') }}
      </label>
      <InputText
        id="username"
        class="w-full mb-2"
        type="text"
        autofocus
        v-model="state.username"
        :invalid="validator.username.$error"
      />
      <Errors :items="validator.username.$errors" />

      <Button
        severity="info"
        class="w-full"
        type="submit"
        :label="t('send')"
        :loading="appStore.isLoading()"
      />
    </form>
  </PageSite>
</template>
