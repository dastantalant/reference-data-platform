<script setup lang="ts">
import PageSite from '@/components/PageSite.vue';
import { useAuth } from '@/services/AuthService';
import { useAppStore } from '@/stores/app';
import { useVuelidate } from '@vuelidate/core';
import { email, required, sameAs } from '@vuelidate/validators';
import { computed, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

const { t } = useI18n();
const router = useRouter();
const appStore = useAppStore();
const { register } = useAuth();

const state = reactive({
  first_name: '',
  last_name: '',
  username: '',
  password: '',
  password_confirmation: '',
  accept: null,
});

const acceptRequired = (value: boolean) => value === true;
const $externalResults = ref<any>({});

const rules = computed(() => ({
  first_name: { required },
  last_name: { required },
  username: { required, email },
  password: { required },
  accept: { acceptRequired },
  password_confirmation: {
    sameAs: sameAs(state.password),
    required
  },
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
      const result = await register(state);

      if (result) {
        router.replace({ name: 'login', query: result });
      }
    }
  } catch (errors) {
    $externalResults.value = errors;
  } finally {
    appStore.endLoading();
  }
};
</script>

<template>
  <PageSite
    :title="t('signUp')"
    v-focustrap
  >
    <form @submit.prevent="onSubmit">
      <label
        for="first_name"
        class="block text-900 text-xl font-medium mb-2"
      >
        {{ t('attributes.first_name') }}
      </label>
      <InputText
        id="first_name"
        class="w-full mb-2"
        type="text"
        autofocus
        v-model="state.first_name"
        :invalid="validator.first_name.$error"
      />
      <Errors :items="validator.first_name.$errors" />

      <label
        for="last_name"
        class="block text-900 text-xl font-medium mb-2"
      >
        {{ t('attributes.last_name') }}
      </label>
      <InputText
        id="last_name"
        type="text"
        mask="99-999999"
        class="w-full mb-2"
        :invalid="validator.last_name.$error"
        v-model="state.last_name"
      />
      <Errors :items="validator.last_name.$errors" />

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
        :class="{ 'p-invalid': validator.username.$error }"
        v-model="state.username"
      />
      <Errors :items="validator.username.$errors" />

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
        :invalid="validator.password.$error"
        :toggle-mask="true"
        :feedback="false"
        v-model="state.password"
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

      <div class="flex align-items-center mt-3 mb-3">
        <Checkbox
          id="accept"
          class="mr-2"
          :invalid="validator.accept.$error"
          :binary="true"
          v-model="state.accept"
        />
        <label
          for="accept"
          :class="{ 'p-error': validator.accept.$error }"
        >
          <i18n-t
            keypath="message.policyInfo"
            tag="p"
          >
            <template #license>
              <router-link
                :to="{ name: 'terms' }"
                target="_blank"
              >
                {{ t('termsAndConditions') }}
              </router-link>
            </template>
            <template #privacity>
              <router-link
                :to="{ name: 'privacity' }"
                target="_blank"
              >
                {{ t('privacityPolicy') }}
              </router-link>
            </template>
          </i18n-t>
        </label>
      </div>

      <Button
        type="submit"
        class="w-full p-3 text-xl mt-3"
        :label="t('confirm')"
        :loading="appStore.isLoading()"
      />
    </form>
  </PageSite>
</template>
