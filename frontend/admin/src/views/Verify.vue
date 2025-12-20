<script setup lang="ts">
import PageSite from '@/components/PageSite.vue';
import { TOAST_LIFE } from '@/global';
import { useAuth } from '@/services/AuthService';
import { useAppStore } from '@/stores/app';
import { useToast } from 'primevue/usetoast';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

const { t } = useI18n();
const { verifyEmail, resendVerifyEmail } = useAuth();
const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const toast = useToast();

const onVerifyEmailClick = async () => {
  appStore.beginLoading();
  try {
    const username = await verifyEmail(route.params.code as string);

    router.replace({ name: 'login', query: {username} });

    setTimeout(() => {
      toast.add({
        severity: 'success',
        summary: t('success'),
        detail: t('message.verifyEmailSuccess'),
        life: TOAST_LIFE,
        closable: false
      })
    }, 300);
  } finally {
    appStore.endLoading();
  }
}

const onResendVerifyEmail = async () => {
  await resendVerifyEmail(route.params.code as string)
  toast.add({
    severity: 'success',
    summary: t('success'),
    detail: t('message.verifyEmailSent'),
    life: TOAST_LIFE,
    closable: false
  })
}

</script>

<template>
  <PageSite
    :title="t('emailVerification')"
  >
    <div class="text-center">
      <p class="text-xl">
        {{ t('welcomeApp') }}
      </p>

      <p class="mt-6">
        {{ t('message.verifyWelcome') }}
      </p>

      <p class="mt-4">
        {{ t('message.verifyConfirmation') }}
      </p>

      <Button
        severity="primary"
        class="mt-20"
        :label="t('verifyMyEmail')"
        @click.prevent="onVerifyEmailClick"
      />

      <div class="flex flex-col items-center justify-center mt-20">
        <i18n-t
          keypath="message.verifyNotReceived"
          tag="p"
          class="justify-center text-wrap"
        >
          <template #click>
            <Button
              link
              as="a"
              :label="t('clickHere')"
              @click.prevent="onResendVerifyEmail"
            />
          </template>
        </i18n-t>
      </div>
    </div>
  </PageSite>
</template>
