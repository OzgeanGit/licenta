import { createRouter, createWebHistory } from 'vue-router';
import CreateDivision from './components/CreateDivision.vue';
import LeagueCreate from './components/LeagueCreate.vue';
import ChessLeagueDashboard from './components/ChessLeagueDashboard.vue';
import UserCreate from './components/UserCreate.vue';
import TournamentPage from './components/TournamentPage.vue';
import DivisionManagement from './components/DivisionManagement.vue';
import MatchmakingPage from './components/MatchmakingPage.vue';
import SeasonResults from './components/SeasonResults.vue';

const routes = [
  {
    path: '/',
    name: 'ChessLeagueDashboard',
    component: ChessLeagueDashboard, // Pagina principala - Panoul Ligii de Sah
  },
  {
    path: '/create-user',
    name: 'UserCreate',
    component: UserCreate, 
  },
  {
    path: '/tournament',
    name: 'TournamentPage',
    component: TournamentPage,
  },
  {
    path: '/create-league',
    name: 'LeagueCreate',
    component: LeagueCreate, 
  },
  {
    path: '/create-division',
    name: 'CreateDivision',
    component: CreateDivision, 
  },
  {
    path: '/divisions',
    name: 'DivisionManagement',
    component: DivisionManagement,
  },
  {
    path: '/matchmaking',
    name: 'MatchmakingPage',
    component: MatchmakingPage,
  },
  {
    path: '/results',
    name: 'SeasonResults',
    component: SeasonResults,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
