<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\MailController;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', [MailController::class, 'sendMail']);

Route::get('/', function () {
    return view('welcome');
});

use App\Http\Controllers\DashboardController;
use App\Http\Controllers\ProfileController;
use App\Http\Controllers\RegisterController;
use App\Http\Controllers\SessionsController;
use App\Http\Controllers\QuestionImportController;
use App\Http\Controllers\AnswerImportController;
use App\Http\Controllers\AnswerController;
use App\Http\Controllers\QuestionController;
use App\Http\Controllers\ChallengeController;
use App\Http\Controllers\SchoolController;
use App\Http\Controllers\AnalyticsController;
use App\Http\Controllers\SperfomanceController;
use App\Http\Controllers\HomeController;
            

Route::get('/reject/{id}', [ApplicantController::class, 'showRejectForm'])->name('reject.form');
Route::post('/reject', [ApplicantController::class, 'reject'])->name('reject');
Route::get('/sign-in', function () {return redirect('sign-in');})->middleware('guest');
Route::get('/dashboard', [DashboardController::class, 'index'])->middleware('auth')->name('dashboard');
Route::get('sign-up', [RegisterController::class, 'create'])->middleware('guest')->name('register');
Route::post('sign-up', [RegisterController::class, 'store'])->middleware('guest');
Route::get('sign-in', [SessionsController::class, 'create'])->middleware('guest')->name('login');
Route::post('sign-in', [SessionsController::class, 'store'])->middleware('guest');
Route::post('verify', [SessionsController::class, 'show'])->middleware('guest');
Route::post('reset-password', [SessionsController::class, 'update'])->middleware('guest')->name('password.update');
Route::get('verify', function () {
	return view('sessions.password.verify');
})->middleware('guest')->name('verify'); 
Route::get('/reset-password/{token}', function ($token) {
	return view('sessions.password.reset', ['token' => $token]);
})->middleware('guest')->name('password.reset');

Route::post('sign-out', [SessionsController::class, 'destroy'])->middleware('auth')->name('logout');
Route::get('profile', [ProfileController::class, 'create'])->middleware('auth')->name('profile');
Route::post('user-profile', [ProfileController::class, 'update'])->middleware('auth');
Route::group(['middleware' => 'auth'], function () {
	Route::get('billing', function () {
		return view('pages.billing');
	})->name('billing');
	Route::get('tables', function () {
		return view('pages.tables');
	})->name('tables');
	Route::get('rtl', function () {
		return view('pages.rtl');
	})->name('rtl');
	Route::get('virtual-reality', function () {
		return view('pages.virtual-reality');
	})->name('virtual-reality');
	Route::get('notifications', function () {
		return view('pages.notifications');
	})->name('notifications');
	Route::get('static-sign-in', function () {
		return view('pages.static-sign-in');
	})->name('static-sign-in');
	Route::get('static-sign-up', function () {
		return view('pages.static-sign-up');
	})->name('static-sign-up');
	Route::get('user-management', function () {
		return view('pages.laravel-examples.user-management');
	})->name('user-management');
	Route::get('user-profile', function () {
		return view('pages.laravel-examples.user-profile');
	})->name('user-profile');


Route::get('import-questions', [QuestionImportController::class, 'importForm'])->name('import.questions.form');
Route::post('import-questions', [QuestionImportController::class, 'import'])->name('import.questions');



Route::get('import-form', [AnswerImportController::class, 'importForm'])->name('import.form');
Route::post('import-answers', [AnswerImportController::class, 'import'])->name('answers.import');



// Route to display all questions
Route::get('questions', [QuestionController::class, 'index'])->name('questions.index');


// Route to display answers
Route::get('answers', [AnswerController::class, 'index'])->name('answers.index');

Route::get('/challenges', [ChallengeController::class, 'index'])->name('challenges.index');
Route::get('/challenges/create', [ChallengeController::class, 'create'])->name('challenges.create');
Route::post('/challenges', [ChallengeController::class, 'store'])->name('challenges.store');
Route::get('/challenges/{challenge}', [ChallengeController::class, 'show'])->name('challenges.show');
Route::get('/challenges/{challenge}/edit', [ChallengeController::class, 'edit'])->name('challenges.edit');
Route::put('/challenges/{challenge}', [ChallengeController::class, 'update'])->name('challenges.update');
Route::delete('/challenges/{challenge}/destroy', [ChallengeController::class, 'destroy'])->name('challenges.destroy');



Route::get('/schools', [SchoolController::class, 'index'])->name('schools.index');
Route::get('/schools/create', [SchoolController::class, 'create'])->name('schools.create');
Route::post('/schools', [SchoolController::class, 'store'])->name('schools.store');
Route::get('/schools/{school}', [SchoolController::class, 'show'])->name('schools.show');
Route::get('/schools/{school}/edit', [SchoolController::class, 'edit'])->name('schools.edit');
Route::put('/schools/{school}', [SchoolController::class, 'update'])->name('schools.update');
Route::delete('/schools/{school}/destroy', [SchoolController::class, 'destroy'])->name('schools.destroy');

//these routes handle analytics

Route::get('/bestschools', [AnalyticsController::class, 'BPschool'])->name('Bestschools');
Route::get('/worstschools/{id}', [AnalyticsController::class, 'showorst'])->name('worstschools');

Route::get('/analytics', [AnalyticsController::class, 'ranking'])->name('analytics.index');
// routes/web.php
Route::get('/incomplete', [AnalyticsController::class, 'showincomplete'])->name('incomplete');


Route::get('/showSP', [ AnalyticsController::class, 'indexSP'])->name('showSP');
Route::get('/SPovertime/{id}', [ AnalyticsController::class, 'showSPerformance'])->name('SPovertime');

Route::get('/school', [AnalyticsController::class, 'index'])->name('school.index');
Route::get('/school/{id}/participants', [AnalyticsController::class, 'showParticipants'])->name('school.showParticipants');
Route::get('/participants/{id}', [AnalyticsController::class, 'showPPerformance'])->name('participants.show');

Route::get('/school-analytics', [AnalyticsController::class, 'schoolAnalytics'])->name('school.analytics');
Route::get('/pupils-analytics', [AnalyticsController::class, 'pupilsAnalytics'])->name('pupils.analytics');




});