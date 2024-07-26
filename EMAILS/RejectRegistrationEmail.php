<?php

namespace App\Mail;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Mail\Mailable;
use Illuminate\Queue\SerializesJobs;

class RejectRegistrationEmail extends Mailable implements ShouldQueue
{
    use Queueable, SerializesJobs;

    public $emailData;

    /**
     * Create a new message instance.
     *
     * @return void
     */
    public function __construct($emailData)
    {
        $this->emailData = $emailData;
    }

    /**
     * Build the message.
     *
     * @return $this
     */
    public function build()
    {
        return $this->subject('Registration Rejected')
                    ->view('emails.rejectRegistration')
                    ->with($this->emailData);
    }
}
