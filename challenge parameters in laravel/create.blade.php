@extends('layouts.app')

@section('content')
    <h1>Create Challenge</h1>
    <form action="{{ route('challenges.store') }}" method="POST">
        @csrf

        <div class="form-group">
            <label for="challengeID">Challenge ID</label>
            <input type="text" id="challengeID" name="challengeID" class="form-control" required>
        </div>

        <div class="form-group">
            <label for="startDate">Start Date</label>
            <input type="date" id="startDate" name="startDate" class="form-control" required>
        </div>

        <div class="form-group">
            <label for="endDate">End Date</label>
            <input type="date" id="endDate" name="endDate" class="form-control" required>
        </div>

        <div class="form-group">
            <label for="noOfQuestions">Number of Questions</label>
            <input type="number" id="noOfQuestions" name="noOfQuestions" class="form-control" required>
        </div>

        <div class="form-group">
            <label for="duration">Duration (in minutes)</label>
            <input type="number" id="duration" name="duration" class="form-control" required>
        </div>

        <div class="form-group">
            <label for="noOfAttempts">Number of Attempts</label>
            <input type="number" id="noOfAttempts" name="noOfAttempts" class="form-control" required>
        </div>

        <button type="submit" class="btn btn-primary">Create Challenge</button>
    </form>
@endsection
