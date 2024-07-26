@extends('layouts.app')

@section('content')
    <h1>Edit Challenge</h1>
    <form action="{{ route('challenges.update', $challenge->challengeID) }}" method="POST">
        @csrf
        @method('PUT')

        <div class="form-group">
            <label for="startDate">Start Date</label>
            <input type="date" id="startDate" name="startDate" class="form-control" value="{{ old('startDate', $challenge->startDate) }}" required>
        </div>

        <div class="form-group">
            <label for="endDate">End Date</label>
            <input type="date" id="endDate" name="endDate" class="form-control" value="{{ old('endDate', $challenge->endDate) }}" required>
        </div>

        <div class="form-group">
            <label for="noOfQuestions">Number of Questions</label>
            <input type="number" id="noOfQuestions" name="noOfQuestions" class="form-control" value="{{ old('noOfQuestions', $challenge->noOfQuestions) }}" required>
        </div>

        <div class="form-group">
            <label for="duration">Duration (in minutes)</label>
            <input type="number" id="duration" name="duration" class="form-control" value="{{ old('duration', $challenge->duration) }}" required>
        </div>

        <div class="form-group">
            <label for="noOfAttempts">Number of Attempts</label>
            <input type="number" id="noOfAttempts" name="noOfAttempts" class="form-control" value="{{ old('noOfAttempts', $challenge->noOfAttempts) }}" required>
        </div>

        <button type="submit" class="btn btn-primary">Update Challenge</button>
    </form>
@endsection
