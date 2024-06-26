<template>
  <div class="season-results-container">
    <div class="background-image"></div> <!-- Fundalul componentei -->
    <div class="navigation-bar">
      <router-link to="/" class="back-button">Înapoi la Dashboard</router-link>
    </div>
    <div class="dropdown-container">
      <!-- Dropdown pentru ligi -->
      <select v-model="selectedLeagueId" @change="onLeagueChange">
        <option disabled value="">Alege o Ligă</option>
        <option v-for="league in leagues" :key="league.id" :value="league.id">{{ league.name }}</option>
      </select>

      <!-- Dropdown pentru divizii -->
      <select v-model="selectedDivisionId" @change="onDivisionChange">
        <option disabled value="">Alege o Divizie</option>
        <option v-for="division in divisions" :key="division.id" :value="division.id">{{ division.name }}</option>
      </select>

      <!-- Dropdown pentru utilizatori -->
      <select v-model="selectedUserId">
        <option disabled value="">Alege un Utilizator</option>
        <option v-for="user in users" :key="user.id" :value="user.id">{{ user.name }}</option>
      </select>

      <!-- Buton pentru vizualizarea evoluției ELO -->
      <button @click="viewEloProgress">Vezi Evoluția ELO</button>
    </div>

    <!-- Container pentru graficul ELO -->
    <div id="eloChartContainer">
      <canvas id="eloChart"></canvas> <!-- Adaugă acest element -->
      <!-- Aici va fi injectat graficul ELO -->
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Chart from 'chart.js/auto';

export default {
  name: "SeasonResults",
  data() {
    return {
      leagues: [],
      divisions: [],
      users: [],
      selectedLeagueId: null,
      selectedDivisionId: null,
      selectedUserId: null,
      eloChartData: null
    };
  },
  methods: {
    async viewEloProgress() {
    if (!this.selectedUserId) {
        alert('Selectează un utilizator pentru a vedea evoluția ELO.');
        return;
    }

    try {
        const response = await axios.get(`http://localhost:8088/api/match/get/matches/user/${this.selectedUserId}`);
        if (response.data.length === 0) {
            alert('Nu există date de istoric ELO pentru utilizatorul selectat.');
            return;
        }
        this.processChartData(response.data);
    } catch (error) {
        console.error('Error fetching ELO history:', error.response || error.message);
        alert('Eroare la încărcarea datelor. Verificați consola pentru detalii.');
    }
},
processChartData(matches) {
    const userEloHistory = matches.map((match, index) => ({
        eloRating: match.player1Id === this.selectedUserId ? match.player1EloAfterMatch : match.player2EloAfterMatch,
        matchIndex: index + 1 
    })).sort((a, b) => a.matchIndex - b.matchIndex); 

    const labels = userEloHistory.map(data => `Meciul ${data.matchIndex}`);
    const dataPoints = userEloHistory.map(data => data.eloRating);

    const ctx = document.getElementById('eloChart').getContext('2d');
    if (this.eloChart) {
        this.eloChart.destroy();
    }
    this.eloChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: 'ELO Rating',
                data: dataPoints,
                fill: false,
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            scales: {
                xAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Numărul meciului'
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'ELO Rating'
                    }
                }]
            }
        }
    });
}
,
    async onLeagueChange() {
      try {
        const response = await axios.get(`http://localhost:8088/api/league/get/divisions/${this.selectedLeagueId}`);
        this.divisions = response.data;
        this.selectedDivisionId = null; 
        this.users = []; 
      } catch (error) {
        console.error('Error fetching divisions:', error);
      }
    },
    async onDivisionChange() {
      try {
        const response = await axios.get(`http://localhost:8088/api/division/get/active/players/${this.selectedDivisionId}`);
        this.users = response.data;
        this.selectedUserId = null; 
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    },
    async fetchLeagues() {
      try {
        const response = await axios.get('http://localhost:8088/api/league/get/all');
        this.leagues = response.data;
      } catch (error) {
        console.error('Error fetching leagues:', error);
      }
    },
    async fetchDivisions(leagueId) {
      try {
        const response = await axios.get(`http://localhost:8088/api/league/get/divisions/${leagueId}`);
        this.divisions = response.data;
      } catch (error) {
        console.error('Error fetching divisions:', error);
      }
    },
    async fetchUsers(divisionId) {
      try {
        const response = await axios.get(`http://localhost:8088/api/division/get/active/players/${divisionId}`);
        this.users = response.data;
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    }
  },
  async mounted() {
    await this.fetchLeagues();
  }
};
</script>

<style scoped>
.season-results-container {
  position: relative;
  width: 100%; 
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

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

.navigation-bar {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 10px 20px;
}

.back-button {
  justify-content: center;
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
  background-color: #e0e0e0;
}

.dropdown-container {
  margin-top: 20px;
}

#eloChartContainer {
  width: 90%;
  max-width: 600px;
  height: 400px;
  margin-top: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
</style>