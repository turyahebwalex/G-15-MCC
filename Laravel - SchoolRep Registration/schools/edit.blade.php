@extends('layouts.app1')

@section('title', 'Edit School')

@section('content')
    <h1>Edit School</h1>

    <form action="{{ route('schools.update', $school->registrationNumber) }}" method="POST">
        @csrf
        @method('PUT')

        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" id="name" name="name" class="form-control" value="{{ old('name', $school->name) }}" required>
            @error('name')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="district">District</label>
            <input type="text" id="district" name="district" class="form-control" value="{{ old('district', $school->district) }}" required>
            @error('district')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="representativeName">Representative Name</label>
            <input type="text" id="representativeName" name="representativeName" class="form-control" value="{{ old('representativeName', $school->representativeName) }}" required>
            @error('representativeName')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="representativeEmail">Representative Email</label>
            <input type="email" id="representativeEmail" name="representativeEmail" class="form-control" value="{{ old('representativeEmail', $school->representativeEmail) }}" required>
            @error('representativeEmail')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <button type="submit" class="btn btn-primary">Update School</button>
    </form>
@endsection
