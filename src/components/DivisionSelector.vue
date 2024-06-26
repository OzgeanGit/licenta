<template>
    <select v-model="selectedDivision" @change="onDivisionChange">
      <option disabled value="">Selectează o Divizie</option>
      <option v-for="division in divisions" :key="division.id" :value="division.id">{{ division.name }}</option>
    </select>
  </template>
  
  <script>
  export default {
    props: ['divisions', 'value'],
    data() {
      return {
        selectedDivision: this.value
      };
    },
    methods: {
      onDivisionChange() {
        this.$emit('input', this.selectedDivision);
      }
    },
    watch: {
      value(newVal) {
        this.selectedDivision = newVal;
      }
    }
  };
  </script>
  