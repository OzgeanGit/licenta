<template>
    <div class="background-image"></div>
    <div class="navigation-bar">
        <!-- Butonul de Înapoi sau navigarea -->
        <router-link to="/" class="back-button">Înapoi la Dashboard</router-link>
    </div>
    <div class="league-create-container">
      <h1>Creează o nouă Ligă</h1>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="leagueName">Nume Ligă</label>
          <input type="text" id="leagueName" v-model="leagueName" required>
        </div>
        <button type="submit">Crează Ligă</button>
      </form>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    name: 'LeagueCreate',
    data() {
      return {
        leagueName: ''
      };
    },
    methods: {
      submitForm() {
        const leagueData = {
          name: this.leagueName
        };
        axios.post('http://localhost:8088/api/league/create', leagueData)
          .then(() => {
            alert('Liga a fost creată cu succes!');
            this.leagueName = ''; 
            this.$router.push({ name: 'DivisionManagement' }); 
          })
          .catch(error => {
            console.error('A apărut o eroare la crearea ligii:', error);
            alert('A apărut o eroare la crearea ligii.');
          });
      }
    }
  };
  </script>
  
  <style scoped>
  .background-image {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-image: url('../assets/background_main.jpg');
    background-size: cover;
    background-attachment: fixed;
    z-index: -1;
  }
  
  .league-create-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 50vh;
    padding: 10px; 
    color:#2c3e50; 
    background: rgba(255, 255, 255, 0.8); 
    border-radius: 15px; 
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2); 
    backdrop-filter: blur(5px);
    width: 80%; 
    max-width: 600px;
    margin: auto; 
    margin-top: 4%;
}
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    color: #333;
  }
  
  .form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
  }
  
  button {
    width: 100%;
    padding: 10px 0;
    background-color: #2c3e50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1em;
    font-weight: 600;
    transition: background-color 0.3s ease;
  }
  
  button:hover {
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