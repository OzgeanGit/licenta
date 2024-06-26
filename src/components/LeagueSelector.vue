<template>
  <select v-model="selectedLeague" @change="onLeagueChange">
    <option disabled value="">Selectează o Ligă</option>
    <option v-for="league in leagues" :key="league.id" :value="league.id">{{ league.name }}</option>
  </select>
</template>

<script>
export default {
  props: ['leagues', 'value'],
  data() {
    return {
      selectedLeague: this.value
    };
  },
  methods: {
    onLeagueChange() {
      this.$emit('input', this.selectedLeague);
      this.$emit('change');
    }
  },
  watch: {
    value(newVal) {
      this.selectedLeague = newVal;
    }
  }
};
</script>


<style scoped>
select {
  padding: 10px;
  margin-bottom: 1rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
  background-color: white;
  font-size: 1rem;
  color: #2c3e50;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  cursor: pointer;
}
</style>
