<script setup lang="ts">
import PageSite from '@/components/PageSite.vue';
import { TOAST_LIFE } from '@/global';
import { useAuth } from '@/services/AuthService';
import { useAppStore } from '@/stores/app';
import useVuelidate from '@vuelidate/core';
import { required, sameAs } from '@vuelidate/validators';
import { useToast } from 'primevue/usetoast';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

const { t } = useI18n();
const { resetPassword } = useAuth();
const appStore = useAppStore();
const toast = useToast();
const route = useRoute();
const router = useRouter();

const state = ref<any>({
  code: route.params.code
});

const rules = computed(() => ({
  password: { required },
  password_confirmation: {
    sameAs: sameAs(state.value.password),
    required
  },
}));

const validator = useVuelidate(rules, state);

const onSubmit = async () => {
  appStore.beginLoading();
  try {
    const isValid = await validator.value.$validate();

    if (isValid) {
      const { username } = await resetPassword(state.value);

      router.replace({name: 'login', query: { username }})

      setTimeout(() => {
        toast.add({
          severity: 'success',
          summary: t('success'),
          detail: t('message.resetPasswordSuccess'),
          life: TOAST_LIFE,
          closable: false
        })
      }, 300);
    }
  } finally {
    appStore.endLoading();
  }
};
</script>

<template>
  <PageSite
    :title="t('passwordReset')"
    v-focustrap
  >
    <form @submit.prevent="onSubmit">
      <label
        for="password"
        class="block text-900 font-medium text-xl mb-2"
      >
        {{ t('attributes.password') }}
      </label>
      <Password
        id="password"
        input-class="w-full"
        class="w-full mb-2"
        autofocus
        v-model="state.password"
        :invalid="validator.password.$error"
        :toggle-mask="true"
        :feedback="false"
      />
      <Errors :items="validator.password.$errors" />

      <label
        for="password_confirmation"
        class="block text-900 font-medium text-xl mb-2"
      >
        {{ t('attributes.password_confirmation') }}
      </label>
      <Password
        id="password_confirmation"
        input-class="w-full"
        class="w-full mb-2"
        :invalid="validator.password_confirmation.$error"
        :toggle-mask="true"
        :feedback="false"
        v-model="state.password_confirmation"
      />
      <small
        v-if="validator.password_confirmation.$error"
        class="p-error"
      >
        {{ t('validation.invalidPasswordConfirmation') }}
      </small>

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
