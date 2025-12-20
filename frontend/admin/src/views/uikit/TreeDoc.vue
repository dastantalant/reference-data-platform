<script setup>
import { ref } from 'vue';

const treeValue = ref(null);
const selectedTreeValue = ref(null);
const treeTableValue = ref(null);
const selectedTreeTableValue = ref(null);

</script>

<template>
  <div class="card">
    <div class="font-semibold text-xl">
      Tree
    </div>
    <Tree
      :value="treeValue"
      selection-mode="checkbox"
      v-model:selectionKeys="selectedTreeValue"
    />
  </div>

  <div class="card">
    <div class="font-semibold text-xl mb-4">
      TreeTable
    </div>
    <TreeTable
      :value="treeTableValue"
      selection-mode="checkbox"
      v-model:selectionKeys="selectedTreeTableValue"
    >
      <Column
        field="name"
        header="Name"
        :expander="true"
      />
      <Column
        field="size"
        header="Size"
      />
      <Column
        field="type"
        header="Type"
      />
    </TreeTable>
  </div>
</template>
