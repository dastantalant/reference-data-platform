<script setup>
import { useToast } from 'primevue/usetoast';
import { ref } from 'vue';

const toast = useToast();
const fileupload = ref();

function upload() {
    fileupload.value.upload();
}

function onUpload() {
    toast.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded', life: 3000 });
}
</script>

<template>
  <div class="grid grid-cols-12 gap-8">
    <div class="col-span-full lg:col-span-6">
      <div class="card">
        <div class="font-semibold text-xl mb-4">
          Advanced
        </div>
        <FileUpload
          name="demo[]"
          @uploader="onUpload"
          :multiple="true"
          accept="image/*"
          :max-file-size="1000000"
          custom-upload
        />
      </div>
    </div>
    <div class="col-span-full lg:col-span-6">
      <div class="card">
        <div class="font-semibold text-xl mb-4">
          Basic
        </div>
        <div class="card flex flex-col gap-6 items-center justify-center">
          <Toast />
          <FileUpload
            ref="fileupload"
            mode="basic"
            name="demo[]"
            accept="image/*"
            :max-file-size="1000000"
            @uploader="onUpload"
            custom-upload
          />
          <Button
            label="Upload"
            @click="upload"
            severity="secondary"
          />
        </div>
      </div>
    </div>
  </div>
</template>
