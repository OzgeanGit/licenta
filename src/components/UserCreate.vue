<template>
  <div class="background-image"></div>
  <div class="navigation-bar">
    <!-- Butonul de Înapoi sau navigarea -->
    <router-link to="/" class="back-button">Înapoi la Dashboard</router-link>
  </div>
  <div class="user-create-container">
    <div class="form-container">
      <h1 class="form-title">Înregistrare Jucător Nou</h1>
      <form @submit.prevent="createUser">
        <div class="form-group">
          <label for="userName">Nume:</label>
          <input type="text" id="userName" v-model="user.name" required>
        </div>
        <div class="form-group">
          <label for="leagueSelect">Selectează Liga:</label>
          <select id="leagueSelect" v-model="selectedLeagueId">
            <option v-for="league in leagues" :key="league.id" :value="league.id">
              {{ league.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label for="userElo">Rating Elo:</label>
          <input type="number" id="userElo" v-model="user.eloRating" required min="800" max="3000">
        </div>
        <button type="submit" class="submit-button">Înregistrează Jucător</button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      user: {
        name: '',
        eloRating: 1000
      },
      leagues: [],
      selectedLeagueId: null 
    };
  },
  created() {
    this.fetchLeagues(); 
  },
  methods: {
    fetchLeagues() {
      axios.get('http://localhost:8088/api/league/get/all')
        .then(response => {
          this.leagues = response.data;
        })
        .catch(error => {
          console.error('There was an error fetching the leagues:', error);
        });
    },
    createUser() {
      const url = 'http://localhost:8088/api/users/create';
      const body = {
        name: this.user.name,
        eloRating: this.user.eloRating,
        leagueId: this.selectedLeagueId 
      };

      axios.post(url, body)
        .then(response => {
          console.log('User created:', response.data);
        })
        .catch(error => {
          console.error('There was an error creating the user:', error);
        });
    }
  }
};
</script>

<style scoped>
.user-create-container {
  position: relative; 
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden; 
}

.background-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw; 
  height: 100vh; 
  background-image: url("../assets/background_main.jpg");
  background-size: cover;
  background-attachment: fixed;
  z-index: -1; 
  overflow: hidden;
}

html, body {
  margin: 0;
  padding: 0;
  overflow: hidden; 
  height: 100%; 
}


.form-container {
  width: 500px;
  background: rgba(255, 255, 255, 0.7);
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 10;
  backdrop-filter: blur(10px);
}

.form-title {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: white;
  -webkit-appearance: none; 
  -moz-appearance: none;    
  appearance: none;         
}

.submit-button {
  width: 60%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #2c3e50;
  color: white;
  cursor: pointer;
}

.submit-button:hover {
  background-color: #3e4e61;
}

.navigation-bar {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.back-button {
  display: block;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.8);
  color: #2c3e50;
  text-align: center;
  border-radius: 5px;
  text-decoration: none;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

</style>
